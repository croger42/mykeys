package org.dpr.mykeys.ihm.crl;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.dpr.mykeys.configuration.KSConfig;
import org.dpr.mykeys.configuration.MkSession;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.crl.CRLEntry;
import org.dpr.mykeys.app.crl.CRLManager;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.ServiceException;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchProviderException;
import java.security.cert.*;
import java.util.*;

public class CRLService {
    final CRLManager manager;
    X509CRL crl;
    File CRLFile;
    Certificate signer;

    public CRLService(Certificate certificate) {
        manager = new CRLManager();
        signer = certificate;
    }

    public X509CRL loadCRL(File f) throws FileNotFoundException {

        CRLFile = f;
        try (InputStream is = new FileInputStream(f)) {
            crl = manager.getCrl(is);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException | CRLException | NoSuchProviderException | CertificateException e) {
            e.printStackTrace();
        }
        return crl;
    }

    public void saveCRL(Date thisUpdate, Date nexUpdate, List<CRLEntry> newEntries)
            throws CRLException, IOException, ServiceException {
        KeyStoreHelper ktools = new KeyStoreHelper();
        if (null == thisUpdate)
            thisUpdate = new Date();
        if (signer.getPrivateKey() == null) {
            signer = ktools.findCertificateByAlias(KSConfig.getInternalKeystores().getStorePKI(), signer.getAlias(), MkSession.password);
        }

        X509CRL newCRL;
        Map filter = new HashMap<BigInteger, CRLEntry>();
        if (crl != null) {
            Set<? extends X509CRLEntry> entries = getRevoked();
            if (entries != null) {
                for (X509CRLEntry x509Entry : entries) {
                    CRLEntry entry = new CRLEntry(x509Entry, "");
                    filter.put(entry.getSerialNumber(), entry);
                }
            }
            for (CRLEntry newEntry : newEntries) {
                filter.putIfAbsent(newEntry.getSerialNumber(), newEntry);
            }
        }
        try (OutputStream output = new FileOutputStream(CRLFile)) {
            newCRL = manager.generateCrl(signer, thisUpdate, nexUpdate, filter.values());
            IOUtils.write(newCRL.getEncoded(), output);
        } catch (OperatorCreationException e) {
            throw new CRLException(e);
        }

    }


    public CRLManager.EtatCrl getValidity() {
        Date now = new Date();

        CRLManager.EtatCrl etatCrl;
        if (now.after(crl.getNextUpdate())) {
            etatCrl = CRLManager.EtatCrl.TO_UPDATE;
        } else {
            etatCrl = CRLManager.EtatCrl.UP_TO_DATE;
        }
        return etatCrl;
    }

    public List<X509Certificate> getChildren(Certificate certificate) {
        return null;
    }

    public Set<? extends X509CRLEntry> getRevoked() {
        if (crl == null)
            return null;

        return crl.getRevokedCertificates();
    }

    public X509CRL getCRL() {
        return crl;
    }
}
