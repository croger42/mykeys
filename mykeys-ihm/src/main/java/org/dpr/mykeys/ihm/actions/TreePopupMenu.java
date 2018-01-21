package org.dpr.mykeys.ihm.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.TreeKeyStorePanel;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.keystore.CreateStoreDialog;
import org.dpr.mykeys.keystore.ImportStoreDialog;

public class TreePopupMenu extends JPopupMenu {

	private JMenuItem addStore;

	private JMenuItem importStore;

	private JMenuItem addCertMenu;

	private JMenuItem importCert;

	private JMenuItem openStore;

	private JMenuItem closeStore;

	private JMenuItem removeStore;

	private JMenuItem deleteStore;

	private JMenuItem exportCert;

	private JMenuItem menuChangePwd;

	private TreeKeyStorePanel treeKeyStoreParent;

	private TreePath path;

	private DefaultMutableTreeNode node;

	class TreePopupAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String action = e.getActionCommand();
			final Object composant = e.getSource();
			JTree tree = (JTree) TreePopupMenu.this.getInvoker();

			TypeAction typeAction = TypeAction.getTypeAction(action);
			JDialog cs;
			JFrame frame = null;
			switch (typeAction) {
			case ADD_STORE:
				frame = (JFrame) tree.getTopLevelAncestor();
				cs = new CreateStoreDialog(frame, true);
				cs.setLocationRelativeTo(frame);
				cs.setVisible(true);
				break;

			case IMPORT_STORE:
				frame = (JFrame) tree.getTopLevelAncestor();
				cs = new ImportStoreDialog(frame, true);
				cs.setLocationRelativeTo(frame);
				cs.setVisible(true);
				break;

			case EXPORT_CERT:
				treeKeyStoreParent.exporterCertificate(node, false);
				break;

			case OPEN_STORE:
				treeKeyStoreParent.openStore(node, false, true);
				break;

			case CLOSE_STORE:
				treeKeyStoreParent.closeStore(node, true);
				break;

			case ADD_CERT:
				try {
					treeKeyStoreParent.addCertificate(node, false);
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case ADD_CERT_AC:
				treeKeyStoreParent.addCertificateAC(node, false);
				break;
			case IMPORT_CERT:
				treeKeyStoreParent.importCertificate(node, false);
				break;

			case REMOVE_STORE:
                KeyStoreValue ksInfo = (KeyStoreValue) node.getUserObject();
				MykeysFrame.removeKeyStore(ksInfo.getPath());
				treeKeyStoreParent.removeNode(node);
				break;
			case CHANGE_PWD:
				treeKeyStoreParent.changePassword(node, false);
				break;
			default:
				break;
			}

		}

	}

	public TreePopupMenu(String string, TreeKeyStorePanel treeKeyStore) {
		super(string);
		this.treeKeyStoreParent = treeKeyStore;
		init();
	}

	private void init() {
        addStore = new JMenuItem(Messages.getString("magasin.new"));
		addStore.addActionListener(new TreePopupAction());
		addStore.setActionCommand(TypeAction.ADD_STORE.getValue());
		addStore.setVisible(false);

        importStore = new JMenuItem(Messages.getString(
				"magasin.load"));
		importStore.addActionListener(new TreePopupAction());
		importStore.setActionCommand(TypeAction.IMPORT_STORE.getValue());
		importStore.setVisible(false);

		addCertMenu = new JMenuItem("Ajouter certificat");
		addCertMenu.addActionListener(new TreePopupAction());
		addCertMenu.setActionCommand(TypeAction.ADD_CERT.getValue());
		addCertMenu.setVisible(false);
        importCert = new JMenuItem(Messages.getString(
				"certificat.import"));
		importCert.addActionListener(new TreePopupAction());
		importCert.setActionCommand(TypeAction.IMPORT_CERT.getValue());
		importCert.setVisible(false);

        exportCert = new JMenuItem(Messages.getString(
				"certificat.export"));
		exportCert.addActionListener(new TreePopupAction());
		exportCert.setActionCommand(TypeAction.EXPORT_CERT.getValue());
		exportCert.setVisible(false);

		openStore = new JMenuItem("Ouvrir Magasin");
		openStore.addActionListener(new TreePopupAction());
		openStore.setActionCommand(TypeAction.OPEN_STORE.getValue());
		openStore.setVisible(false);
		
	
		
		closeStore = new JMenuItem("Fermer magasin");

		closeStore.addActionListener(new TreePopupAction());
		closeStore.setActionCommand(TypeAction.CLOSE_STORE.getValue());
		closeStore.setVisible(false);

		removeStore = new JMenuItem("Retirer du gestionnaire");
		removeStore.addActionListener(new TreePopupAction());
		removeStore.setActionCommand(TypeAction.REMOVE_STORE.getValue());
		removeStore.setVisible(false);

		deleteStore = new JMenuItem("Suppression physique");
		deleteStore.addActionListener(new TreePopupAction());
		deleteStore.setActionCommand(TypeAction.DELETE_STORE.getValue());
		deleteStore.setVisible(false);

        menuChangePwd = new JMenuItem(Messages.getString("magasin.change.password"));
		menuChangePwd.addActionListener(new TreePopupAction());
		menuChangePwd.setActionCommand(TypeAction.CHANGE_PWD.getValue());
		menuChangePwd.setVisible(false);
		
		add(addStore);
		add(importStore);
		add(addCertMenu);
		add(importCert);
		add(exportCert);
		// add(openStore);
		// add(closeStore);
		add(removeStore);
		add(deleteStore);
		add(menuChangePwd);
	}

	/**
	 * @return the node
	 */
	public DefaultMutableTreeNode getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(DefaultMutableTreeNode node) {
		addStore.setVisible(false);
		addCertMenu.setVisible(false);
		importCert.setVisible(false);
		exportCert.setVisible(false);
		openStore.setVisible(false);
		closeStore.setVisible(false);
		removeStore.setVisible(false);
		deleteStore.setVisible(false);
		menuChangePwd.setVisible(false);
		this.node = node;
		if (node == null || node.getParent() == null) {
			addStore.setVisible(true);
			importStore.setVisible(true);

        } else if (node.getUserObject() instanceof KeyStoreValue) {
            KeyStoreValue ksInfo = (KeyStoreValue) node.getUserObject();

			if (ksInfo.isOpen()) {
				addCertMenu.setVisible(true);
				importCert.setVisible(true);
				closeStore.setVisible(true);
			} else {
				openStore.setVisible(true);
			}
			if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)){
				removeStore.setVisible(true);
				menuChangePwd.setVisible(true);
			}else{
				
			}

		} else if (node.getUserObject() instanceof CertificateValue) {
			CertificateValue certInfo = (CertificateValue) node.getUserObject();

			exportCert.setVisible(true);

		}
	}

}
