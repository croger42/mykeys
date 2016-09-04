package org.dpr.mykeys.ihm.windows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.app.CertificateInfo;
import org.dpr.mykeys.app.CommonsActions;
import org.dpr.mykeys.app.CrlInfo;
import org.dpr.mykeys.app.InternalKeystores;
import org.dpr.mykeys.app.KeyStoreInfo;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.ProviderUtil;
import org.dpr.mykeys.ihm.MyKeys;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.JSpinnerDate;
import org.dpr.swingutils.LabelValuePanel;

public class CreateCrlDialog extends JDialog {
	public static final Log log = LogFactory.getLog(ListPanel.class);
	// JTextField x509PrincipalC;
	// JTextField x509PrincipalO;
	// JTextField x509PrincipalL;
	// JTextField x509PrincipalST;
	// JTextField x509PrincipalE;
	// JTextField x509PrincipalCN;

	JTextField tfDirectoryOut;
	LabelValuePanel infosPanel;

	// CertificateInfo certInfo = new CertificateInfo();

	boolean isAC = false;
	public JFileChooser jfc;

	public static void main(String[] args) {
		CreateCrlDialog crlDiag = new CreateCrlDialog(null, true);
		crlDiag.build();
		crlDiag.setVisible(true);
	}

	private void build() {
		ProviderUtil.initBC();
		init(null);
		this.pack();

	}

	public CreateCrlDialog(JFrame owner, boolean modal) {

		super(owner, modal);

	}

	public CreateCrlDialog(JFrame frame, KeyStoreInfo ksInfo, CertificateInfo certificateInfo) {
		super(frame, true);
		init(certificateInfo);
	}

	private void init(CertificateInfo certInfo) {

		DialogAction dAction = new DialogAction();
		// FIXME:

		setTitle("Création d'une liste de révocation");

		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelInfo.setMinimumSize(new Dimension(400, 100));

		
		// for (String algo : ProviderUtil.SignatureList) {
		// mapAlgoSig.put(algo, algo);
		// }
		Map<String, String> mapAC = getMapAC(certInfo);
		getInfoPanel(null, mapAC);
		panelInfo.add(infosPanel);

		FileSystemView fsv = FileSystemView.getFileSystemView();
		File f = fsv.getDefaultDirectory();

		JLabel jl5 = new JLabel("Fichier en sortie");

		tfDirectoryOut = new JTextField(40);
		tfDirectoryOut.setText(f.getAbsolutePath());
		JButton jbChoose2 = new JButton("...");
		jbChoose2.addActionListener(dAction);
		jbChoose2.setActionCommand("CHOOSE_OUT");

		JPanel jpDirectory2 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		jpDirectory2.add(jl5);
		jpDirectory2.add(tfDirectoryOut);
		jpDirectory2.add(jbChoose2);

		JButton jbOK = new JButton("Valider");
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
		JButton jbCancel = new JButton("Annuler");
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		jp.add(panelInfo);
		jp.add(jpDirectory2);
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
	private void getInfoPanel(Map<String, String> mapAlgoSig, Map<String, String> mapAC) {
		infosPanel = new LabelValuePanel();

		infosPanel.put("Emetteur", JComboBox.class, "emetteur", mapAC, "");

		infosPanel.put("Alias (nom du certificat)", "alias", "");
		infosPanel.putEmptyLine();

		
		// subject
		infosPanel.putEmptyLine();
		Calendar calendar = Calendar.getInstance();

		infosPanel.put(MyKeys.getMessage().getString("certinfo.notBefore"), JSpinnerDate.class, "notBefore",
				calendar.getTime(), true);
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		infosPanel.put(MyKeys.getMessage().getString("certinfo.notAfter"), JSpinnerDate.class, "notAfter",
				calendar.getTime(), true);
		infosPanel.putEmptyLine();

		infosPanel.put("Numéros de série brùlés", "numcers", "");
		// 275136453

		infosPanel.putEmptyLine();

	}

	private Map getMapAC(CertificateInfo certInfo) {
		Map<String, String> mapAC = null;
		if (certInfo != null) {
			mapAC = new HashMap<String, String>();
			mapAC.put(certInfo.getAlias(), certInfo.getAlias());
			return mapAC;
		}
		try {
			mapAC = TreeKeyStorePanel.getListCerts(InternalKeystores.getACPath(), "JKS", InternalKeystores.password);
		} catch (Exception e) {
			//
		}
		if (mapAC == null) {
			mapAC = new HashMap<String, String>();
		}
		mapAC.put(" ", " ");
		return mapAC;
	}

	public class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("OK")) {

				Map<String, Object> elements = infosPanel.getElements();
				log.trace(elements.get("alias"));
				Set<String> keys = elements.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()) {
					String key = it.next();
				}
				if (elements.get("alias") == null) {
					MykeysFrame.showError(CreateCrlDialog.this, "alias obligatoire");
					return;
				}
				CrlInfo crlInfo = new CrlInfo();

				if (elements.get("numcers") != null) {
					// List<String> lstNumCer =
					String numcers = (String) elements.get("numcers");
					String[] strNumcers = numcers.split(",");
					for (String numcer : strNumcers) {
						if (!numcer.isEmpty())
							crlInfo.addNumSer(numcer);
					}

				}

				// certInfo.setX509PrincipalMap(elements);
				HashMap<String, String> subjectMap = new HashMap<String, String>();
				crlInfo.setName((String) elements.get("alias"));
				crlInfo.setThisUpdate((Date) elements.get("notBefore"));
				crlInfo.setNextUpdate((Date) elements.get("notAfter"));
				crlInfo.setPath(tfDirectoryOut.getText());

				KeyTools ktools = new KeyTools();
				// CertificateInfo certSign =
				// ktools.getCertificateACByAlias((String)
				// elements.get("emetteur"));

				CommonsActions cActions = new CommonsActions();

				try {

					cActions.generateCrl((String) elements.get("emetteur"), crlInfo);
					// FIXME: add crl to tree
					// ktools.generateCrl(certSign, crlInfo, privateKey);
					CreateCrlDialog.this.setVisible(false);

				} catch (Exception e) {
					MykeysFrame.showError(CreateCrlDialog.this, e.getMessage());
					e.printStackTrace();
				}
			} else if (command.equals("CANCEL")) {
				CreateCrlDialog.this.setVisible(false);
			}

		}
	}

}
