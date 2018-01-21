package org.dpr.mykeys.keystore;

import static org.dpr.mykeys.utils.MessageUtils.getMessage;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.StringUtils;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyToolsException;
import org.dpr.mykeys.app.TamperedWithException;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.swingutils.JFieldsPanel;
import org.dpr.swingutils.LabelValuePanel;

public class ChangePasswordDialog extends JDialog {

	private JTextField tfDirectory;

	// JComboBox ksType;
	// JPasswordField pwd1;
	// JPasswordField pwd2;
    private LabelValuePanel infosPanel;

    private KeyStoreValue ksInfo;

	// Map<String, String> elements = new HashMap<String, String>();


    public ChangePasswordDialog(JFrame frame, KeyStoreValue ksInfo) {
		super(frame, true);
		this.ksInfo=ksInfo;
		init();
		this.pack();
	}

    private void init() {
		DialogAction dAction = new DialogAction();
		setTitle(getMessage("magasin.change.password"));
		JPanel jp = new JPanel();
		BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(bl);
		setContentPane(jp);

		

		infosPanel = new LabelValuePanel();


		infosPanel.put(getMessage("label.old.password"), JPasswordField.class, "pwd_old", "", true);
		infosPanel.put(getMessage("label.new.password"), JPasswordField.class, "pwd_new", "", true);
		infosPanel.put(getMessage("label.confirm.password"), JPasswordField.class, "pwd_new2", "", true);

		infosPanel.putEmptyLine();


        JButton jbOK = new JButton(Messages.getString("button.confirm"));
		jbOK.addActionListener(dAction);
		jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
		jbCancel.addActionListener(dAction);
		jbCancel.setActionCommand("CANCEL");
		JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

		jp.add(infosPanel);

		jp.add(jf4);

	}

    class DialogAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			Map<String, Object> elements = infosPanel.getElements();
			String command = event.getActionCommand();
			 if (command.equals("OK")) {
				if (StringUtils.isBlank((String) elements.get("pwd_old")) || StringUtils.isBlank((String) elements.get("pwd_new")) || StringUtils.isBlank((String) elements.get("pwd_new2") )) {
					MykeysFrame.showError(ChangePasswordDialog.this, "Champs invalides");
					return;
				}
				if (!elements.get("pwd_new").equals(elements.get("pwd_new2"))) {
					MykeysFrame.showError(ChangePasswordDialog.this, getMessage("error.match.password"));
					return;
				}
				ksInfo.setPassword(((String)elements.get("pwd_old")).toCharArray());
				KeyStoreHelper service = new KeyStoreHelper();
				try {
					service.changePassword(ksInfo, ((String)elements.get("pwd_new")).toCharArray());
					ksInfo.setOpen(false);
					ChangePasswordDialog.this.setVisible(false);
				} catch (TamperedWithException | KeyToolsException e) {
					MykeysFrame.showError(ChangePasswordDialog.this, e.getLocalizedMessage());
				}

			

			} else if (command.equals("CANCEL")) {
				ChangePasswordDialog.this.setVisible(false);
			}

		}

	}

	/**
	 * @author Christophe Roger
	 * @date 8 mai 2009
	 */
    private class KeyStoreFileFilter extends FileFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File arg0) {
			// TODO Auto-generated method stub
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public void updateKeyStoreList() {
		// TODO Auto-generated method stub

	}

	public String correctExtension(String name, String typeKS) {
		if (!name.toUpperCase().endsWith("JKS") && typeKS.equals("JKS")) {
			name = name + ".jks";
		}
		if (!name.toUpperCase().endsWith("p12") && typeKS.equals("PKCS12")) {
			name = name + ".p12";
		}
		return name;
	}

	public String getDataDir() {

		String dir = KSConfig.getUserCfg().getString("data.dir");
		if (dir == null) {
			// if (OSInfo.getOs().equals(OS.UNIX)) {
			// dir = KSConfig.getCfgPath();
			// } else {
			// document dir in windows
			File f = FileSystemView.getFileSystemView().getDefaultDirectory();
			File data = new File(f, getMessage("default.datadir"));
			data.mkdirs();
			dir = data.getAbsolutePath();
			// }
		}
		return dir;
	}

	public void createKeyStore(StoreFormat format, String text, char[] charArray) {
		// TODO Auto-generated method stub

	}
}
