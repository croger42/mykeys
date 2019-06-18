package org.dpr.mykeys.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;

public class KeyTools {
    public static final String BEGIN_PEM = "-----BEGIN CERTIFICATE-----";
    public static final String END_PEM = "-----END CERTIFICATE-----";
    public static final String BEGIN_KEY = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String END_KEY = "-----END RSA PRIVATE KEY-----";
    public static final int NUM_ALLOWED_INTERMEDIATE_CAS = 0;
    public static String EXT_P12 = ".p12";
    public static String EXT_PEM = ".pem";
    public static String EXT_DER = ".der";
    // FIXME:en création de magasin si l'extension est saisie ne pas la mettre 2
    // fois.
    // FIXME: ne pas autoriser la saisie de la clé privée dans les magasins
    // internes
    private final Log log = LogFactory.getLog(KeyTools.class);

    public static void main(String[] args) {
        KeyTools test = new KeyTools();

        Security.addProvider(new BouncyCastleProvider());


    }

    /**
     * get a random BigInteger
     *
     * @param numBits
     * @return
     */
    public static BigInteger RandomBI(int numBits) {
        SecureRandom random = new SecureRandom();
        // byte bytes[] = new byte[20];
        // random.nextBytes(bytes);
        BigInteger bi = new BigInteger(numBits, random);
        return bi;

    }

    /**
     * Key pair generation
     *
     * @param algo
     * @param keyLength
     * @param certModel
     * @deprecated replace with own code
     */
    @Deprecated
    public void keyPairGen(String algo, int keyLength, CertificateValue certModel) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("generating keypair: " + algo + " keypair: " + keyLength);
            }

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo, "BC");
            keyGen.initialize(keyLength);

            KeyPair keypair = keyGen.genKeyPair();
            certModel.setPrivateKey(keypair.getPrivate());
            certModel.setPublicKey(keypair.getPublic());

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    protected void saveKeyStore(KeyStore ks, KeyStoreValue ksInfo) throws KeyToolsException {
        log.debug("saveKeyStore ");
        try (OutputStream fos = new FileOutputStream(new File(ksInfo.getPath()))) {
            ks.store(fos, ksInfo.getPassword());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new KeyToolsException("Echec de sauvegarde du magasin impossible:" + ksInfo.getPath(), e);
        }
    }

    protected CRLDistPoint getDistributionPoints(X509Certificate certX509) {

        X509CertificateObject certificateImpl = (X509CertificateObject) certX509;

        byte[] extension = certificateImpl.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());

        if (extension == null) {
            if (log.isWarnEnabled()) {
                log.warn("Pas de CRLDistributionPoint pour: " + certificateImpl.getSubjectDN());//
            }
            return null;
        }

        CRLDistPoint distPoints = null;

        try {
            distPoints = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(extension));
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Extension de CRLDistributionPoint non reconnue pour: " + certificateImpl.getSubjectDN());//
            }
            if (log.isDebugEnabled()) {
                log.debug(e);
            }

        }
        return distPoints;

    }

}
