package org.dpr.mykeys.ihm.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.Border;

import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.CrlInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.app.X509Constants;
import org.dpr.mykeys.app.KeyStoreInfo.StoreModel;
import org.dpr.mykeys.ihm.KeyStoreUI;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.TreeKeyStorePanel;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CreateCrlDialog extends JDialog implements ItemListener {

    // JTextField x509PrincipalC;
    // JTextField x509PrincipalO;
    // JTextField x509PrincipalL;
    // JTextField x509PrincipalST;
    // JTextField x509PrincipalE;
    // JTextField x509PrincipalCN;
    LabelValuePanel infosPanel;

    CrlInfo crlInfo;

    CertificateInfo certInfo = new CertificateInfo();

    boolean isAC = false;

    public CreateCrlDialog(JFrame owner,
	    boolean modal) {

	super(owner, modal);

	init();
	this.pack();

    }

    private void init() {

	DialogAction dAction = new DialogAction();
	//FIXME:

	setTitle("Cr�ation d'une liste de r�vocation");

	JPanel jp = new JPanel();
	BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
	jp.setLayout(bl);
	setContentPane(jp);

	JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
	panelInfo.setMinimumSize(new Dimension(400, 100));

	Map<String, String> mapKeyLength = new HashMap<String, String>();
	mapKeyLength.put("512 bits", "512");
	mapKeyLength.put("1024 bits", "1024");
	mapKeyLength.put("2048 bits", "2048");
	mapKeyLength.put("4096 bits", "4096");
	// fill with provider's available algorithms
	Map<String, String> mapAlgoKey = new LinkedHashMap<String, String>();
	for (String algo : ProviderUtil.KeyPairGeneratorList) {
	    mapAlgoKey.put(algo, algo);
	}
	// fill with provider's available algorithms
	Map<String, String> mapAlgoSig = new LinkedHashMap<String, String>();
	for (String algo : ProviderUtil.SignatureList) {
	    mapAlgoSig.put(algo, algo);
	}

	getInfoPanel(isAC, mapKeyLength, mapAlgoKey, mapAlgoSig);
	panelInfo.add(infosPanel);

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

	JButton jbOK = new JButton("Valider");
	jbOK.addActionListener(dAction);
	jbOK.setActionCommand("OK");
	JButton jbCancel = new JButton("Annuler");
	jbCancel.addActionListener(dAction);
	jbCancel.setActionCommand("CANCEL");
	JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

	jp.add(panelInfo);
	jp.add(checkPanel);
	jp.add(jf4);

    }

    /**
     * .
     * 
     * <BR>
     * 
     * <pre>
     * <b>Algorithme : </b>
     * DEBUT
     *    
     * FIN
     * </pre>
     * 
     * @param mapKeyLength
     * @param mapAlgoKey
     * @param mapAlgoSig
     * 
     * @param isAC2
     * @return
     */
    private void getInfoPanel(boolean isAC, Map<String, String> mapKeyLength,
	    Map<String, String> mapAlgoKey, Map<String, String> mapAlgoSig) {
	infosPanel = new LabelValuePanel();
	Map<String, String> mapAC = null;
	try {
	    mapAC = TreeKeyStorePanel.getListCerts(InternalKeystores.getACPath(),
		    "JKS", InternalKeystores.password);
	} catch (Exception e) {
	    //
	}
	if (mapAC == null) {
	    mapAC = new HashMap<String, String>();
	}
	mapAC.put(" ", " ");
	infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

	    infosPanel.put("Alias (nom du certificat)", "alias", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Taille cl� publique", JComboBox.class, "keyLength",
		    mapKeyLength, "2048 bits");
	    infosPanel.put("Algorithme cl� publique", JComboBox.class,
		    "algoPubKey", mapAlgoKey, "RSA");
	    infosPanel.put("Algorithme de signature", JComboBox.class,
		    "algoSig", mapAlgoSig, "SHA256WithRSAEncryption");
	    // subject
	    infosPanel.putEmptyLine();
	    Calendar calendar = Calendar.getInstance();

	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"),
		    JSpinnerDate.class, "notBefore", calendar.getTime(), true);
	    calendar.add(Calendar.DAY_OF_YEAR, 60);
	    infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"),
		    JSpinnerDate.class, "notAfter", calendar.getTime(), true);
	    infosPanel.putEmptyLine();
	    infosPanel.put("Nom (CN)", "CN", "Nom");
	    infosPanel.put("Pays (C)", "C", "FR");
	    infosPanel.put("Organisation (O)", "O", "Orga");
	    infosPanel.put("Section (OU)", "OU", "D�veloppement");
	    infosPanel.put("Localit� (L)", "L", "Saint-Etienne");
	    infosPanel.put("Rue (ST)", "SR", "");
	    infosPanel.put("Email (E)", "E", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Point de distribution des CRL (url)", "CrlDistrib",
		    "");
	    infosPanel.put("Policy notice", "PolicyNotice", "");
	    infosPanel.put("Policy CPS", "PolicyCPS", "");
	    infosPanel.putEmptyLine();
	    infosPanel.put("Mot de passe cl� priv�e", JPasswordField.class,
		    "pwd1", "", true);
	    infosPanel.put("Confirmer le mot de passe", JPasswordField.class,
		    "pwd2", "", true);
	

    }

    public class DialogAction extends AbstractAction {

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
		    KeyStoreUI.showError(CreateCrlDialog.this,
			    "Champs obligatoires");
		    return;
		}

		// certInfo.setX509PrincipalMap(elements);
		HashMap<String, String> subjectMap = new HashMap<String, String>();
		certInfo.setAlgoPubKey((String) elements.get("algoPubKey"));
		certInfo.setAlgoSig((String) elements.get("algoSig"));
		certInfo.setKeyLength((String) elements.get("keyLength"));
		certInfo.setAlias((String) elements.get("alias"));
		certInfo.setNotBefore((Date) elements.get("notBefore"));
		certInfo.setNotAfter((Date) elements.get("notAfter"));
		KeyTools ktools = new KeyTools();
		char[] pkPassword = ((String) elements.get("pwd1"))
			.toCharArray();

		certInfo.setSubjectMap(elements);
		certInfo.setPassword(pkPassword);
		X509Certificate[] xCerts = null;

		try {
		    certInfo.setCrlDistributionURL(((String) elements
			    .get("CrlDistrib")));
		    certInfo.setPolicyNotice(((String) elements
			    .get("PolicyNotice")));
		    certInfo.setPolicyCPS(((String) elements.get("PolicyCPS")));

		    xCerts = ktools.genererX509(certInfo,
			    (String) elements.get("emetteur"), isAC);

		    //ktools.generateCrl(certSign, crlInfo, privateKey);
		    CreateCrlDialog.this.setVisible(false);

		} catch (Exception e) {
		    KeyStoreUI.showError(CreateCrlDialog.this,
			    e.getMessage());
		    e.printStackTrace();
		}
	    } else if (command.equals("CANCEL")) {
		CreateCrlDialog.this.setVisible(false);
	    }

	}

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
	Object source = e.getItemSelectable();
	JCheckBox jc = (JCheckBox) source;
	String val = jc.getText();
	for (int i = 0; i < X509Constants.keyUsageLabel.length; i++) {
	    if (val.equals(X509Constants.keyUsageLabel[i])) {
		certInfo.getKeyUsage()[i] = jc.isSelected();
		return;
	    }
	}

    }

}
