package org.dpr.mykeys.ihm.components;

import static org.dpr.swingtools.ImageUtils.createImageIcon;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.profile.CertificateTemplate;
import org.dpr.mykeys.app.profile.ProfileServices;
import org.dpr.mykeys.ihm.CancelCreationException;
import org.dpr.mykeys.ihm.actions.TypeAction;
import org.dpr.mykeys.ihm.windows.ListCertRenderer;
import org.dpr.mykeys.ihm.windows.certificate.*;
import org.dpr.mykeys.template.CreateTemplateDialog;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.LabelValuePanel;

@SuppressWarnings("serial")
public class ListProfilePanel extends CertificateListPanel implements DropTargetListener {
    private static final Log log = LogFactory.getLog(ListProfilePanel.class);

    private ProfileServices profileService = new ProfileServices(KSConfig.getProfilsPath());

    class ListTransferHandler extends TransferHandler {
		DataFlavor certFlavor;

		public ListTransferHandler() {
			try {
				String certType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
						+ org.dpr.mykeys.app.certificate.CertificateValue.class.getName() + "\"";
				certFlavor = new DataFlavor(certType);
			} catch (ClassNotFoundException e) {
				log.trace("ClassNotFound: " + e.getMessage());
			}
		}
	}

	private DetailPanel detailPanel;
    private KeysAction actions;

	/**
	 * @author Buck
	 *
	 */
    class CertListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {

				log.trace(e.getSource().getClass());
				if (e.getSource() instanceof JList) {
					if (((JList) e.getSource()).getSelectedValue() instanceof ChildInfo) {
						displayDetail((ChildInfo) ((JList) e.getSource()).getSelectedValue());
						if (ksInfo.isOpen()) {
							exportButton.setEnabled(true);
							deleteButton.setEnabled(true);
						}
					}

				}
			}

		}

	}

	// Map<String, String> elements = new HashMap<String, String>();
	LabelValuePanel infosPanel;

    private NodeInfo ksInfo;

    private ActionPanel dAction;

    private JPanel jp;

    private JLabel titre = new JLabel();

    private JButton addCertButton;
    private JButton addCertProfButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton deleteButton;
    private JToggleButton unlockButton;

    private DefaultListModel listModel;
    private JImgList listCerts;
    private DropTarget dropTarget;

    private ListProfilePanel() {
		super();

		init();

	}

	private void init() {
		// Create the DropTarget and register
		// it with the JPanel.
		dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true, null);
		dAction = new ActionPanel();
		// setBackground(new Color(125,0,0));
		// BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		// this.setLayout(bl);

		// titre = new GradientLabel("Gestion des certificats");
		// add(titre);
		jp = new JPanel(new BorderLayout());
		final ImageIcon icon = createImageIcon("/images/Locked.png");

		JToolBar toolBar = new JToolBar("Still draggable");
		toolBar.setFloatable(false);
		listModel = new DefaultListModel();
		ListSelectionListener listListener = new CertListListener();

		listCerts = new JImgList(listModel);

		listCerts.addListSelectionListener(listListener);
		ListCertRenderer renderer = new ListCertRenderer();
		listCerts.setCellRenderer(renderer);
		listCerts.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listCerts.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listCerts.setVisibleRowCount(-1);
		listCerts.setDragEnabled(true);
		// listCerts.setTransferHandler(new ListTransferHandler());
		addCertButton = new JButton(createImageIcon("/images/add-cert.png"));
		addCertProfButton = new JButton(createImageIcon("/images/add-cert-pro.png"));
		unlockButton = new JToggleButton(createImageIcon("/images/Locked.png"));
		unlockButton.setActionCommand(TypeAction.OPEN_STORE.getValue());
		// unlockButton.setIcon(createImageIcon("/images/Locked.png"));
		unlockButton.setDisabledIcon(createImageIcon("/images/Unlocked.png"));
		addCertButton.setActionCommand(TypeAction.ADD_CERT.getValue());
		addCertProfButton.setActionCommand(TypeAction.ADD_CERT_PROF.getValue());
        addCertProfButton.setToolTipText("create cert from template");
		importButton = new JButton("Import");
		importButton.setActionCommand(TypeAction.IMPORT_CERT.getValue());
		exportButton = new JButton("Export");
		exportButton.setActionCommand(TypeAction.EXPORT_CERT.getValue());
		// FIXME libelles
		deleteButton = new JButton("Supprimer");
		deleteButton.setActionCommand(TypeAction.DELETE_CERT.getValue());
		deleteButton.setEnabled(false);
		exportButton.setEnabled(false);
		importButton.setEnabled(false);
		actions = new KeysAction(this);
		exportButton.addActionListener(actions);
		importButton.addActionListener(actions);
		unlockButton.addActionListener(actions);
		deleteButton.addActionListener(actions);
		addCertProfButton.addActionListener(actions);
		toolBar.add(titre);
		toolBar.add(unlockButton);
		toolBar.add(addCertButton);
		toolBar.add(addCertProfButton);
		toolBar.add(importButton);
		toolBar.add(exportButton);
		toolBar.add(deleteButton);
		toolBar.addSeparator();

		JScrollPane listScroller = new JScrollPane(listCerts);
		// listScroller.setPreferredSize(new Dimension(450, 80));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);
		jp.add(toolBar, BorderLayout.PAGE_START);
		jp.add(listScroller, BorderLayout.CENTER);

		// jp.add(listScroller);
		add(jp);
		// jp.add();
		// jp.add(new JLabel("Contenu du certificat"));
		// jp.setVisible(true);
		jp.setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dpr.mykeys.ihm.components.IListPanel#updateInfo(org.dpr.mykeys.app.
     * KeyStoreValue)
	 */

	public void updateInfo(NodeInfo info) {
		jp.setVisible(false);
		// jp.removeAll();
		// jp.revalidate();
		if (info == null) {
			return;
		}
		ksInfo = info;
		listCerts.clearSelection();
		listModel.removeAllElements();

		for (ChildInfo ci : ksInfo.getChildList()) {
			listModel.addElement(ci);
		}

		addCertButton.removeActionListener(actions);
		//addCertProfButton.removeActionListener(actions);
		if (ksInfo.isOpen()) {
			unlockButton.setSelected(false);
			unlockButton.setEnabled(false);

			addCertButton.setEnabled(true);
			importButton.setEnabled(true);

			addCertButton.addActionListener(actions);
			//addCertProfButton.addActionListener(actions);
			listCerts.setShowImage(false);

		} else {

			importButton.setEnabled(false);
			exportButton.setEnabled(false);
			deleteButton.setEnabled(false);
			addCertButton.setEnabled(false);
			addCertProfButton.setEnabled(false);
			unlockButton.setSelected(false);
			// unlockButton.setIcon(createImageIcon("/images/Locked.png"));
			unlockButton.setEnabled(true);
			listCerts.setShowImage(true);
		}
		titre.setText(ksInfo.getName());

		jp.revalidate();
		jp.setVisible(true);
	}

	/**
	 * .
	 * 
	 * <BR>
	 *
	 *
	 * @return
	 * @throws KeyToolsException
	 */

    class ActionPanel extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			if (command.equals("CHECK_OCSP")) {
				log.trace("OCSP");

			} else if (command.equals("OK")) {

				KeyTools kt = new KeyTools();

			} else if (command.equals("CANCEL")) {
				// ExportCertificateDialog.this.setVisible(false);
			}

		}

	}

	private void displayDetail(ChildInfo info) {
		detailPanel.updateInfo(info);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dpr.mykeys.ihm.components.IListPanel#setDetailPanel(org.dpr.mykeys.
	 * ihm.components.DetailPanel)
	 */

	public void setDetailPanel(DetailPanel detailPanel) {
		this.detailPanel = detailPanel;
	}

	/**
	 * @return the ksInfo
	 */
	public NodeInfo getKsInfo() {
		return ksInfo;
	}

	/**
	 * @param ksInfo
	 *            the ksInfo to set
	 */
    public void setKsInfo(KeyStoreValue ksInfo) {
		this.ksInfo = ksInfo;
	}

    class KeysAction implements ActionListener {

        KeysAction(JComponent frameSource) {
			super();
			this.frameSource = frameSource;
			// this.ksInfo = ksInfo;
		}

		private JComponent frameSource;

        // private KeyStoreValue ksInfo;

		@Override
		public void actionPerformed(ActionEvent e) {
			final String action = e.getActionCommand();
			final Object composant = e.getSource();

			TypeAction typeAction = TypeAction.getTypeAction(action);
			JDialog cs;
			JFrame frame = null;
			switch (typeAction) {
			// case ADD_STORE:
			// frame = (JFrame) tree.getTopLevelAncestor();
			// cs = new CreateStoreDialog(frame, true);
			// cs.setLocationRelativeTo(frame);
			// cs.setVisible(true);
			// break;
			//
			// case IMPORT_STORE:
			// frame = (JFrame) tree.getTopLevelAncestor();
			// cs = new ImportStoreDialog(frame, true);
			// cs.setLocationRelativeTo(frame);
			// cs.setVisible(true);
			// break;

			// case EXPORT_CERT:
			// treeKeyStoreParent.exporterCertificate(node, false);
			// break;
			//
			case OPEN_STORE:
				if (openStore(false, true)) {
				}
				updateInfo(ListProfilePanel.this.ksInfo);
				break;
			//
			// case CLOSE_STORE:
			// treeKeyStoreParent.closeStore(node, true);
			// break;

			case ADD_CERT:
				addElement(ksInfo, false);
				// addCertificate(ksInfo, false);
				break;
			case ADD_CERT_PROF: 
				addCertFromPRofile(ksInfo, false);
				// addCertificate(ksInfo, false);
				break;
			case IMPORT_CERT:
				importCertificate(ksInfo, false);
				break;

			case EXPORT_CERT:
				if (listCerts != null && listCerts.getSelectedValue() != null
						&& listCerts.getSelectedValue() instanceof CertificateValue) {
                    showExportCertificatesFrame(ksInfo, listCerts.getSelectedValuesList());
				}
				break;
			case DELETE_CERT:
				if (listCerts != null && listCerts.getSelectedValue() != null
						&& listCerts.getSelectedValue() instanceof CertificateValue) {
                    CertificateTemplate certInfo = (CertificateTemplate) listCerts.getSelectedValue();
					if (DialogUtil.askConfirmDialog(null, Messages.getString("delete.certificat.confirm", certInfo.getName()))) {
						try {
							profileService.delete( certInfo);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				break;

			default:
				break;
			}

		}

	}

	public void addElement(NodeInfo info, boolean b) {
	
		JFrame frame = (JFrame) this.getTopLevelAncestor();
		SuperCreate cs = null;
        if (info instanceof KeyStoreValue) {
			try {
				cs = CertificateCreateFactory.getCreateDialog(frame, (KeyStoreValue) info, true);
			} catch (CancelCreationException e) {
				//creation cancelled
				return;
			}
		} else {
            cs = new CreateTemplateDialog(frame, true);
		}
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(info);

		return;
	}
	
	public void addCertFromPRofile(NodeInfo info, boolean b) {
		
		JFrame frame = (JFrame) this.getTopLevelAncestor();
		SuperCreate cs = null;
        if (info instanceof KeyStoreValue) {
            cs = new CreateCertProfilDialog(frame, (KeyStoreValue) info, true);
		} 
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(info);

		return;
	}



	/**
	 * .
	 * 
	 * <BR>
	 * 
	 *
	 * @param info
	 * @param certificateInfo
	 */
    public void showDeleteCertificateFrame(NodeInfo info, CertificateValue certificateInfo) {
        KeyStoreValue kinfo = (KeyStoreValue) info;
	
		KeyStoreHelper ksv = new KeyStoreHelper(kinfo);
		try {
            ksv.removeCertificate(kinfo, certificateInfo);

		} catch (Exception e1) {
            DialogUtil.showError(this, e1.getMessage());
			e1.printStackTrace();
		}
		updateInfo(ksInfo);

	}

	public void exporterCertificate(NodeInfo info, List<CertificateValue> certificates, boolean b) {
        KeyStoreValue kinfo = (KeyStoreValue) info;
		JFrame frame = (JFrame) this.getTopLevelAncestor();

		ExportCertificateDialog cs = new ExportCertificateDialog(frame, kinfo, certificates, true);
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);

	}

	public void importCertificate(NodeInfo info, boolean b) {
        KeyStoreValue kinfo = (KeyStoreValue) info;
		JFrame frame = (JFrame) this.getTopLevelAncestor();

		ImportCertificateDialog cs = new ImportCertificateDialog(frame, kinfo, true);
		cs.setLocationRelativeTo(frame);
		cs.setResizable(false);
		cs.setVisible(true);
		updateInfo(kinfo);

	}
	
	

	public boolean openStore(boolean useInternalPwd, boolean expand) {

		// if (ksInfo.getStoreType().equals(StoreType.INTERNAL)) { //
		// equals(StoreModel.CASTORE))
		// // {
		// useInternalPwd = true;
		// }
		// ask for password
		if (ksInfo.isProtected()) {

            KeyStoreValue kstInfo = (KeyStoreValue) ksInfo;
			if (kstInfo.getPassword() == null) {
                char[] password = DialogUtil.showPasswordDialog(this);

				if (password == null || password.length == 0) {
					return false;
				}

				kstInfo.setPassword(password);
			}

		}

		try {
			ksInfo.open();

			ksInfo.setOpen(true);

		} catch (Exception e1) {
            DialogUtil.showError(this, e1.getMessage());
			e1.printStackTrace();
			return false;
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragEnter(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragExit(java.awt.dnd.
	 * DropTargetEvent)
	 */
	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#dragOver(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dpr.mykeys.ihm.components.IListPanel#drop(java.awt.dnd.
	 * DropTargetDropEvent)
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();

			try {
				boolean result = false;

				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					result = dropFile(transferable);
				} else {
					result = false;
				}

				dtde.dropComplete(result);

			} catch (Exception e) {
				log.error("Exception while handling drop ", e);
				dtde.rejectDrop();
			}
		} else {
			log.info("Drop target rejected drop");
			dtde.dropComplete(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dpr.mykeys.ihm.components.IListPanel#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub

	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable)
			throws IOException, UnsupportedFlavorException {
		List fileList = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
		File transferFile = (File) fileList.get(0);

		final String transferURL = transferFile.getAbsolutePath();

		return true;
	}

}
