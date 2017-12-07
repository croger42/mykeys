package org.dpr.mykeys.profile;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.certificate.CertificateUtils;
import org.dpr.mykeys.app.profile.Profil;
import org.dpr.swingutils.LabelValuePanel;

public class ProfilDetailPanel extends LabelValuePanel {

	private Profil info;

	public ProfilDetailPanel(Profil info) {
		this.info=info;
		getPanel();
	}
  
	public void getPanel(){
		//infosPanel = new LabelValuePanel();
		this.put(KSConfig.getMessage().getString("label.name"),
				JTextField.class, "", info.getName(), false);
		
		this.putEmptyLine();
	this.put(KSConfig.getMessage().getString("x509.pubkeysize"),
				JTextField.class, "keyLength",
				info.getValue("keyLength"), false);
	
	this.put(KSConfig.getMessage().getString("x509.pubkeyalgo"),
	JTextField.class, "algoPubKey", info.getValue("algoPubKey"), false);
	this.put(KSConfig.getMessage().getString("x509.sigalgo"),
	JTextField.class, "algoPubKey", info.getValue("algoSig"), false);
	this.putEmptyLine();
	this.put(KSConfig.getMessage().getString("certinfo.duration"),
	JTextField.class, "algoPubKey", info.getValue("duration"), false);
	this.putEmptyLine();
	this.put(KSConfig.getMessage().getString("x509.subject.organisation"),
	JTextField.class, "algoPubKey", info.getValue("O"), false);
	this.put(KSConfig.getMessage().getString("x509.subject.location"),
	JTextField.class, "algoPubKey", info.getValue("L"), false);
	this.put(KSConfig.getMessage().getString("x509.subject.organisationUnit"),
	JTextField.class, "algoPubKey", info.getValue("OU"), false);
	
	KeyUsage ku = new KeyUsage(info.getIntValue("keyUSage"));
	int ku2 = info.getIntValue("keyUSage");
	if ((ku2 & KeyUsage.digitalSignature) == KeyUsage.digitalSignature)
		System.out.println("xx");
	if ((ku2 & KeyUsage.decipherOnly) == KeyUsage.decipherOnly)
		System.out.println("yy");
	if ((ku2 & KeyUsage.dataEncipherment) == KeyUsage.dataEncipherment)
		System.out.println("zz");
	

	this.put(KSConfig.getMessage().getString("x509.subject.organisationUnit"),
			JTextArea.class, "algoPubKey", CertificateUtils.keyUsageToString(info.getIntValue("keyUSage")), false);

	
//	name=gg
//			C=FR
//			=3
//			PolicyCPS=
//			CrlDistrib=
//			PolicyNotice=
//			SR=
//		
//			algoSig=SHA256WithRSAEncryption
//			=RSA
//			emetteur=\ 
//			keyLength=2048
	

//		// this.put("Clé publique", JTextArea.class, "pubKey",
//		// X509Util.toHexString(info.getPublicKey().getEncoded()," ",
//		// false),false);
	
//		this.put(KSConfig.getMessage().getString("x509.sigalgo"),
//				JTextField.class, "algoSig", info.getAlgoSig(), false);
//		this.put(KSConfig.getMessage().getString("x509.startdate"),
//				JSpinnerDate.class, "notBefore", info.getNotBefore(), false);
//		this.put(KSConfig.getMessage().getString("x509.enddate"),
//				JSpinnerDate.class, "notAfter", info.getNotAfter(), false);
//		this.putEmptyLine();
//		this.put(KSConfig.getMessage().getString("x509.serial"),
//				JTextField.class, "numser", info.getCertificate()
//						.getSerialNumber().toString(), false);
//		this.put(KSConfig.getMessage().getString("x509.issuer"),
//				JTextField.class, "emetteur", info.getCertificate()
//						.getIssuerX500Principal().toString(), false);
//		if (info.getSubjectMap() != null) {
//			Iterator<String> iter = info.getSubjectMap().keySet().iterator();
//			while (iter.hasNext()) {
//				String key = iter.next();
//				String name;
//				try {
//					name = KSConfig.getMessage().getString(
//							X509Util.getMapNames().get(key));
//				} catch (Exception e) {
//					name = key;
//				}
//				String value = info.getSubjectMap().get(key);
//				if (value.startsWith("#")) {
//					value = new String(Hex.decode(value.substring(1,
//							value.length())));
//				}
//				this.put(name, JTextField.class, "", value, false);
//			}
//		}
//
//		String keyUsage = info.keyUsageToString();
//		if (keyUsage != null) {
//			this.put("Utilisation (key usage)", JLabel.class, "keyUsage",
//					keyUsage, false);
//		}
	}
}