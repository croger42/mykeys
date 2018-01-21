package org.dpr.mykeys.ihm.windows.certificate;

import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.certificate.Usage;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.app.keystore.StoreModel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;

public class CreateCertificatDialog extends SuperCreate implements ItemListener {

    public CreateCertificatDialog(JFrame owner, KeyStoreValue ksInfo,
                                  boolean modal) {

        super(owner, modal);
        this.ksInfo = ksInfo;
        if (ksInfo == null) {
            isAC = false;
        } else if (ksInfo.getStoreModel().equals(StoreModel.CASTORE)) {
            isAC = true;
        }
        init();
        this.pack();
        //this.setVisible(true);

    }

    public static void main(String[] args) {
        JFrame f = null;
        CreateCertificatDialog cr = new CreateCertificatDialog(f, null,
                false);
    }

    private class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN")) {

            } else if (command.equals("OK")) {
                Map<String, Object> elements = infosPanel.getElements();
                Set<String> keys = elements.keySet();
                Iterator<String> it = keys.iterator();
                while (it.hasNext()) {
                    String key = it.next();
                }
                if (elements.get("alias") == null
                        || elements.get("pwd1") == null) {
                    MykeysFrame.showError(CreateCertificatDialog.this,
                            "Champs obligatoires");
                    return;
                }

                // certInfo.setX509PrincipalMap(elements);
                HashMap<String, String> subjectMap = new HashMap<>();
                FillUtils.fillCertInfo(elements, certInfo);
                certInfo.setAlias((String) elements.get("alias"));
                certInfo.setNotBefore((Date) elements.get("notBefore"));
                certInfo.setNotAfter((Date) elements.get("notAfter"));
                KeyTools ktools = new KeyTools();
                KeyStoreHelper kserv = new KeyStoreHelper();
                char[] pkPassword = ((String) elements.get("pwd1"))
                        .toCharArray();

                certInfo.setSubjectMap(elements);

                if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                    certInfo.setPassword(KSConfig.getInternalKeystores().getPassword().toCharArray());
                } else {
                    certInfo.setPassword(pkPassword);
                }


                X509Certificate[] xCerts = null;

                try {
                    certInfo.setCrlDistributionURL(((String) elements
                            .get("CrlDistrib")));
                    certInfo.setPolicyNotice(((String) elements
                            .get("PolicyNotice")));
                    certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

                    KeyStore ks = kserv.getKeystore();

                    CertificateHelper certServ = new CertificateHelper(null);
                    CertificateValue infoEmetteur = kserv.fillCertInfo(ks, (String) elements.get("emetteur"));
                    String aliasIssuer = (String) elements.get("emetteur");
                    CertificateValue issuer = null;
                    if (null != aliasIssuer)
                        issuer = kserv.findCertificateAndPrivateKeyByAlias(ksInfo, (String) elements.get("emetteur"));
                    //FIXME
                    xCerts = (X509Certificate[]) certServ.createCertificate(certInfo, issuer, Usage.CODESIGNING).getCertificateChain();

                    kserv.addCertToKeyStore(ksInfo, xCerts, certInfo, KSConfig.getInternalKeystores().getPassword().toCharArray());
                    CreateCertificatDialog.this.setVisible(false);

                } catch (Exception e) {
                    MykeysFrame.showError(CreateCertificatDialog.this,
                            e.getMessage());
                    e.printStackTrace();
                }
            } else if (command.equals("CANCEL")) {
                CreateCertificatDialog.this.setVisible(false);
            }

        }

    }

}
