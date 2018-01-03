package org.dpr.mykeys.ihm.windows.certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.certificate.CertificateHelper;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.windows.OkCancelPanel;
import org.dpr.mykeys.keystore.CertificateType;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class SuperCreate extends JDialog implements ItemListener {

    public static final Log log = LogFactory.getLog(SuperCreate.class);
    protected LabelValuePanel infosPanel;

    protected CertificateType typeCer;
    protected LabelValuePanel durationPanel;
    protected KeyStoreValue ksInfo;
    protected CertificateValue certInfo = new CertificateValue();
    protected boolean isAC = false;

    public SuperCreate() {
        super();
    }

    public SuperCreate(Frame owner) {
        super(owner);
    }

    public SuperCreate(Dialog owner) {
        super(owner);
    }

    public SuperCreate(Window owner) {
        super(owner);
    }

    public SuperCreate(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public SuperCreate(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    private static String getDefaultDuration(CertificateType standard) {
        return "3";
    }

    protected void init() {

        String a = null;
        final JPanel panel = new JPanel();
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changEvent) {
                JRadioButton aButton = (JRadioButton) changEvent.getSource();

                if (aButton.isSelected()) {
                    typeCer = CertificateType.valueOf(aButton.getName());

                }

            }
        };

        BoxLayout bls = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(bls);
        final JRadioButton button1 = new JRadioButton("Client");
        button1.setSelected(true);
        button1.setName(CertificateType.STANDARD.toString());
        button1.addChangeListener(changeListener);
        final JRadioButton button2 = new JRadioButton("Serveur");
        button2.setName(CertificateType.SERVER.toString());
        final JRadioButton button3 = new JRadioButton("Signature de code");
        button3.setName(CertificateType.CODE_SIGNING.toString());
        button2.addChangeListener(changeListener);
        button3.addChangeListener(changeListener);
        ButtonGroup vanillaOrMod = new ButtonGroup();
        vanillaOrMod.add(button1);
        vanillaOrMod.add(button2);
        vanillaOrMod.add(button3);
        panel.add(button1);
        // panel.add(button2);
        // panel.add(button3);
//Server Authentication (1.3.6.1.5.5.7.3.1).
//
//        Other "common" types of X.509 certs are Client Authentication (1.3.6.1.5.5.7.3.2), Code Signing (1.3.6.1.5.5.7.3.3), and a handful of others are used for various encryption and authentication schemes.
        JOptionPane.showMessageDialog(this.getParent(), panel, Messages.getString("type.certificat"), 1, null);
        DialogAction dAction = new DialogAction();

        System.out.println(typeCer);
        if (isAC) {
            setTitle(Messages.getString("ac.creation.title"));
        } else {
            setTitle(Messages.getString("certificat.creation.title"));
        }
        JPanel jp = new JPanel();
        //BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(new VerticalLayout());
        //jp.setLayout(bl);
        setContentPane(jp);


        LabelValuePanel subjectPanel = new LabelValuePanel();
        if (isAC)
            PanelUtils.addSubjectToPanel(CertificateType.AC, subjectPanel);
        else
            PanelUtils.addSubjectToPanel(CertificateType.STANDARD, subjectPanel);
        createInfoPanel(isAC, null, null, null);
        //panelInfo.add(infosPanel);

        // JPanel panelInfo2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel checkPanel = new JPanel(new GridLayout(0, 3));

        Border border = BorderFactory.createTitledBorder("Key usage");
        checkPanel.setBorder(border);
        for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
            JCheckBox item = new JCheckBox(X509Constants.keyUsageLabel[i]);
            item.addItemListener(this);
            if ((isAC && i == 5) || (isAC && i == 6)) {
                item.setSelected(true);
            }
            checkPanel.add(item);
        }
        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());

        jp.add(subjectPanel);
        jp.add(getDurationPanel(3));
        jp.add(getPubKeyPanel());
        jp.add(getSignaturePanel());
        jp.add(infosPanel);
        infosPanel.getElements().putAll(subjectPanel.getElements());


        jp.add(cp);
        jp.add(checkPanel);
        jp.add(new OkCancelPanel(dAction, FlowLayout.RIGHT));
        // Show/hide the "Controls"
        JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText("Show/Hide Search Panel");
        this.add(toggle);

    }

    private Component getPubKeyPanel() {
        Map<String, String> mapAlgoKey = new LinkedHashMap<String, String>();
        for (String algo : ProviderUtil.getKeyPairGeneratorList()) {
            mapAlgoKey.put(algo, algo);
        }
        Map<String, String> mapKeyLength = new LinkedHashMap<String, String>();
        mapKeyLength.put("512 bits", "512");
        mapKeyLength.put("1024 bits", "1024");
        mapKeyLength.put("2048 bits", "2048");
        mapKeyLength.put("4096 bits", "4096");
        mapKeyLength.put("8192 bits*", "8192");
        mapKeyLength.put("16384 bits*", "16384");

        LabelValuePanel pubKeyPanel = new LabelValuePanel();
        pubKeyPanel.addTitle(Messages.getString("publickey.info.title"));
        pubKeyPanel.put("Taille clé publique", JComboBox.class, "keyLength", mapKeyLength, "2048 bits");
        pubKeyPanel.put("Algorithme clé publique", JComboBox.class, "algoPubKey", mapAlgoKey, "RSA");
        pubKeyPanel.putEmptyLine();
        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());
        cp.add(pubKeyPanel);
        infosPanel.getElements().putAll(pubKeyPanel.getElements());
        return cp;
    }

    private Component getSignaturePanel() {
        Map<String, String> mapAC = null;
        try {
            mapAC = TreeKeyStorePanel.getListCerts(KSConfig.getInternalKeystores().getACPath(), "JKS",
                    KSConfig.getInternalKeystores().getPassword());
        } catch (Exception e) {
            //
        }
        if (mapAC == null) {
            mapAC = new HashMap<String, String>();
        }
        mapAC.put(" ", " ");

        // fill with provider's available algorithms
        Map<String, String> mapAlgoSig = new LinkedHashMap<String, String>();
        for (String algo : ProviderUtil.SignatureList) {
            mapAlgoSig.put(algo, algo);
        }


        LabelValuePanel sigPanel = new LabelValuePanel();
        sigPanel.addTitle(Messages.getString("signature.info.title"));
        sigPanel.put(Messages.getString("x509.sigalgo"), JComboBox.class, "algoSig", mapAlgoSig,
                "SHA256WITHRSA");

        sigPanel.put(Messages.getString("x509.issuer"), JComboBox.class, "emetteur", mapAC, "");
        sigPanel.putEmptyLine();
        JXCollapsiblePane cp = new JXCollapsiblePane();

        // JXCollapsiblePane can be used like any other container
        cp.setLayout(new BorderLayout());
        cp.add(sigPanel);
        infosPanel.getElements().putAll(sigPanel.getElements());
        return cp;
    }

    /**
     * .
     *
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * @param isAC
     * @return
     */
    private LabelValuePanel createInfoPanel(boolean isAC, Map<String, String> mapKeyLength,
                                            Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {

        if (infosPanel == null) {
            infosPanel = new LabelValuePanel();

            infosPanel = new LabelValuePanel();
            infosPanel.put(Messages.getString("x509.alias"), "alias", "");


            if (isAC) {


                infosPanel.putEmptyLine();


                // subject
                infosPanel.putEmptyLine();
                Calendar calendar = Calendar.getInstance();

                infosPanel.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore",
                        calendar.getTime(), true);
                calendar.add(Calendar.YEAR, 5);
                infosPanel.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter",
                        calendar.getTime(), true);
                //infosPanel.put("aaa", JTextField.class, "notAfter", calendar.getTime(), true);
                infosPanel.put(Messages.getString("certinfo.duration"), "duration", "3");
                infosPanel.putEmptyLine();
                PanelUtils.addSubjectToPanel(CertificateType.AC, infosPanel);

                infosPanel.putEmptyLine();
                infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
                infosPanel.put("Policy notice", "PolicyNotice", "");
                infosPanel.put("Policy CPS", "PolicyCPS", "");
                infosPanel.putEmptyLine();
                if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                    infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", KSConfig.getInternalKeystores().getPassword(),
                            false);
                    infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
                            KSConfig.getInternalKeystores().getPassword(), false);
                }

            } else {


                infosPanel.putEmptyLine();


                // subject
                infosPanel.putEmptyLine();
                Calendar calendar = Calendar.getInstance();

//                infosPanel.put(Messages.getString("certinfo.duration"), "duration",
//                        getDefaultDuration(CertificateType.STANDARD));
//                JCheckBox cbDuration = new JCheckBox(Messages.getString("extended_mode"));
//
//                cbDuration.setName("extendDuration");
//                cbDuration.addItemListener(this);

                //  infosPanel.put("", cbDuration);
                //    infosPanel.put(getDurationPanel(3));
                infosPanel.putEmptyLine();


                infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib", "");
                infosPanel.put("Policy notice", "PolicyNotice", "");
                infosPanel.put("Policy CPS", "PolicyCPS", "");
                infosPanel.putEmptyLine();
                if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                    infosPanel.put("Mot de passe clé privée", JPasswordField.class, "pwd1", KSConfig.getInternalKeystores().getPassword(),
                            true);
                    infosPanel.put("Confirmer le mot de passe", JPasswordField.class, "pwd2",
                            KSConfig.getInternalKeystores().getPassword(), true);
                }
            }
        }
        return infosPanel;

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
        String name = jc.getName();
        if (name != null && name.equals("extendDuration")) {
            durationPanel.setVisible(jc.isSelected());
            // this.pack();

        }
        String val = jc.getText();
        for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
            if (val.equals(X509Constants.keyUsageLabel[i])) {
                certInfo.getKeyUsage()[i] = jc.isSelected();
                return;
            }
        }

    }

    public LabelValuePanel getDurationPanel(int duration) {


        Calendar calendar = Calendar.getInstance();
        if (durationPanel == null) {
            durationPanel = new LabelValuePanel();

            durationPanel.addTitle(Messages.getString("valid.period.title"));
            durationPanel.put(Messages.getString("x509.startdate"), JSpinnerDate.class, "notBefore",
                    calendar.getTime(), true);
            calendar.add(Calendar.YEAR, duration);
            durationPanel.put(Messages.getString("x509.enddate"), JSpinnerDate.class, "notAfter",
                    calendar.getTime(), true);
            // durationPanel.setVisible(false);
            durationPanel.putEmptyLine();
            infosPanel.getElements().putAll(durationPanel.getElements());

        }
        return durationPanel;

    }

    public class DialogAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN")) {

            } else if (command.equals("OK")) {
                try {
                    fillCertInfo();

                    KeyStoreHelper kserv = new KeyStoreHelper();

                    certInfo.setIssuer((String) infosPanel.getElements().get("emetteur"));
                    CertificateHelper certServ = new CertificateHelper(certInfo);

                    CertificateValue issuer = null;
                    if (null != certInfo.getIssuer() && !certInfo.getIssuer().trim().isEmpty())
                        issuer = kserv.findCertificateAndPrivateKeyByAlias(KSConfig.getInternalKeystores().getStoreAC(), certInfo.getIssuer());

                    CertificateValue newCertificate = certServ.createCertificate(isAC, issuer);
                    kserv.addCertToKeyStore(ksInfo, newCertificate, ksInfo.getPassword());
                    SuperCreate.this.setVisible(false);

                } catch (Exception e) {

                    log.error("certificate generation error", e);
                    MykeysFrame.showError(SuperCreate.this, e.getMessage());

                }

            } else if (command.equals("CANCEL")) {
                SuperCreate.this.setVisible(false);
            }

        }

        public void fillCertInfo() {
            Map<String, Object> elements = infosPanel.getElements();
            Set<String> keys = elements.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
            }
            if (elements.get("alias") == null
                    || (elements.get("pwd1") == null && !ksInfo.getStoreType().equals(StoreLocationType.INTERNAL))) {
                MykeysFrame.showError(SuperCreate.this, "Champs obligatoires");
                return;
            }

            // certInfo.setX509PrincipalMap(elements);
            HashMap<String, String> subjectMap = new HashMap<String, String>();
            FillUtils.fillCertInfo(elements, certInfo);
            certInfo.setAlias((String) elements.get("alias"));
            certInfo.setNotBefore((Date) elements.get("notBefore"));
            certInfo.setNotAfter((Date) elements.get("notAfter"));
            if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                char[] pkPassword = ((String) elements.get("pwd1")).toCharArray();
                certInfo.setPassword(pkPassword);
            }
            certInfo.setSubjectMap(elements);

            certInfo.setCrlDistributionURL(((String) elements.get("CrlDistrib")));
            certInfo.setPolicyNotice(((String) elements.get("PolicyNotice")));
            certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

        }

    }


}