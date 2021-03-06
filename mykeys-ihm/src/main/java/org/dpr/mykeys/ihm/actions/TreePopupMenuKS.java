package org.dpr.mykeys.ihm.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.ihm.AppManager;
import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.ServiceException;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.ihm.components.treekeystore.TreeKeyStoreActions;
import org.dpr.mykeys.ihm.listeners.EventKeystoreListener;
import org.dpr.mykeys.ihm.listeners.HelpMouseListener;
import org.dpr.mykeys.ihm.windows.MykeysFrame;
import org.dpr.mykeys.ihm.keystore.CreateStoreDialog;
import org.dpr.mykeys.ihm.keystore.ImportStoreDialog;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class TreePopupMenuKS extends JPopupMenu implements TreePopupMenu {
    public static final Log log = LogFactory.getLog(TreePopupMenuKS.class);
    private JMenuItem addStore;


    private JMenuItem importStore;

    private JMenuItem addCertMenu;

    private JMenuItem importCert;

    private JMenuItem openStore;

    private JMenuItem closeStore;

    private JMenuItem removeStore;

    private JMenuItem openExplorer;

    private JMenuItem deleteStore;

    private JMenuItem exportCert;

    private JMenuItem menuChangePwd;

    private final TreeKeyStoreActions treeKeyStoreParent;

    private TreePath path;

    private DefaultMutableTreeNode node;

    private List<EventKeystoreListener> keystoreListenerList = new ArrayList<>();

    public TreePopupMenuKS(String string, TreeKeyStoreActions treeKeyStore) {
        super(string);
        this.treeKeyStoreParent = treeKeyStore;

        init();
    }

    private void init() {

        addStore = new JMenuItem(Messages.getString("magasin.new"));
        addStore.addActionListener(new TreePopupAction());
        addStore.setActionCommand(TypeAction.ADD_STORE.getValue());
        addStore.setVisible(false);
        addStore.addMouseListener(new HelpMouseListener("new_store"));
        importStore = new JMenuItem(Messages.getString(
                "magasin.load"));
        importStore.addActionListener(new TreePopupAction());
        importStore.setActionCommand(TypeAction.IMPORT_STORE.getValue());
        importStore.setVisible(false);
        importStore.addMouseListener(new HelpMouseListener("import_store"));
        addCertMenu = new JMenuItem("Ajouter certificat");
        addCertMenu.addActionListener(new TreePopupAction());
        addCertMenu.setActionCommand(TypeAction.ADD_CERT.getValue());
        addCertMenu.setVisible(false);
        addCertMenu.addMouseListener(new HelpMouseListener("add_cert"));
        importCert = new JMenuItem(Messages.getString(
                "certificat.import"));
        importCert.addActionListener(new TreePopupAction());
        importCert.setActionCommand(TypeAction.IMPORT_CERT.getValue());
        importCert.setVisible(false);
        importCert.addMouseListener(new HelpMouseListener("import_cert"));
        exportCert = new JMenuItem(Messages.getString(
                "certificat.export"));
        exportCert.addActionListener(new TreePopupAction());
        exportCert.setActionCommand(TypeAction.EXPORT_CERT.getValue());
        exportCert.setVisible(false);
        exportCert.addMouseListener(new HelpMouseListener("export_cert"));
        openStore = new JMenuItem("Ouvrir Magasin");
        openStore.addActionListener(new TreePopupAction());
        openStore.setActionCommand(TypeAction.OPEN_STORE.getValue());
        openStore.setVisible(false);
        openStore.addMouseListener(new HelpMouseListener("unlock_store"));

        closeStore = new JMenuItem("Close");

        closeStore.addActionListener(new TreePopupAction());
        closeStore.setActionCommand(TypeAction.CLOSE_STORE.getValue());
        closeStore.setVisible(false);

        removeStore = new JMenuItem(Messages.getString("store.unload"));
        removeStore.addActionListener(new TreePopupAction());
        removeStore.setActionCommand(TypeAction.REMOVE_STORE.getValue());
        removeStore.setVisible(false);
        removeStore.addMouseListener(new HelpMouseListener("remove_store"));
        openExplorer = new JMenuItem(Messages.getString("open.explorer"));
        openExplorer.addActionListener(new TreePopupAction());
        openExplorer.setActionCommand(TypeAction.OPEN_EXPLORER.getValue());
        openExplorer.setVisible(false);
        openExplorer.addMouseListener(new HelpMouseListener("show_explorer"));
        deleteStore = new JMenuItem("Suppression physique");
        deleteStore.addActionListener(new TreePopupAction());
        deleteStore.setActionCommand(TypeAction.DELETE_STORE.getValue());
        deleteStore.setVisible(false);
        deleteStore.addMouseListener(new HelpMouseListener("delete_store"));
        menuChangePwd = new JMenuItem(Messages.getString("magasin.change.password"));
        menuChangePwd.addActionListener(new TreePopupAction());
        menuChangePwd.setActionCommand(TypeAction.CHANGE_PWD.getValue());
        menuChangePwd.setVisible(false);
        menuChangePwd.addMouseListener(new HelpMouseListener("changeit_store"));
        add(addStore);
        add(importStore);
        add(addCertMenu);
        add(importCert);
        add(exportCert);
        add(openStore);
        // add(closeStore);
        add(openExplorer);
        add(removeStore);
        add(deleteStore);
        add(menuChangePwd);
    }

    /**
     * @return the node
     */
    @Override
    public DefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    @Override
    public void setNode(DefaultMutableTreeNode node) {
        addStore.setVisible(false);
        addCertMenu.setVisible(false);
        importCert.setVisible(false);
        exportCert.setVisible(false);
        openStore.setVisible(false);
        closeStore.setVisible(false);
        openExplorer.setVisible(false);
        removeStore.setVisible(false);
        deleteStore.setVisible(false);
        menuChangePwd.setVisible(false);
        importStore.setVisible(false);
        this.node = node;
        if (node == null) {
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
            if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                removeStore.setVisible(true);
                deleteStore.setVisible(true);
                menuChangePwd.setVisible(true);
                openExplorer.setVisible(true);
            }
            // stores imported from previous mk version
            if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL) && ksInfo.getName().startsWith("previous")) {
                removeStore.setVisible(true);
                deleteStore.setVisible(true);
                addCertMenu.setVisible(false);
                importCert.setVisible(false);
            }
        } else if (node.getUserObject() instanceof Certificate) {
            Certificate certInfo = (Certificate) node.getUserObject();

            exportCert.setVisible(true);

        }
    }

    //    @Override
    public void registerListener(EventListener listener) {

    }

    class TreePopupAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String action = e.getActionCommand();
            final Object composant = e.getSource();
            JTree tree = (JTree) TreePopupMenuKS.this.getInvoker();

            TypeAction typeAction = TypeAction.getTypeAction(action);
            JDialog cs;
            JFrame frame = null;
            KeyStoreValue ksInfo = null;
            switch (typeAction) {
                case ADD_STORE:
                    frame = (JFrame) tree.getTopLevelAncestor();
                    CreateStoreDialog csd = new CreateStoreDialog(frame, true);
                    csd.setLocationRelativeTo(frame);
                    boolean result = csd.showDialog();
                    if (result) {

                            fireKeystoreListChanged();

                    }
                    break;

                case IMPORT_STORE:
                    frame = (JFrame) tree.getTopLevelAncestor();
                    ImportStoreDialog csi = new ImportStoreDialog(frame, true);
                    csi.setLocationRelativeTo(frame);
                    Object resultDialog = csi.showDialog();
                    if (resultDialog != null && (Boolean)resultDialog) {

                        fireKeystoreListChanged();

                    }

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
                    ksInfo = (KeyStoreValue) node.getUserObject();
                    AppManager.removeKeyStore(ksInfo.getPath());
                    treeKeyStoreParent.removeNode(node);
                    break;

                case OPEN_EXPLORER:
                    ksInfo = (KeyStoreValue) node.getUserObject();
                    Desktop desktop = Desktop.getDesktop();
//                   if (desktop.isSupported(Desktop.Action.OPEN))
//                   {
                    File dirToOpen = null;
                    try {
                        dirToOpen = new File(ksInfo.getPath());
                        desktop.open(dirToOpen.getParentFile());
                    } catch (IOException e1) {
                        log.error(e);
                    }
//                   }
                    break;
                case DELETE_STORE:
                    ksInfo = (KeyStoreValue) node.getUserObject();
                    if (DialogUtil.askConfirmDialog(treeKeyStoreParent.getComponent(), Messages.getString("delete.certificat.confirm", ksInfo.getName()))) {
                        try {

                            Path fileToDeletePath = Paths.get(ksInfo.getPath());
                            Files.delete(fileToDeletePath);
                        } catch (IOException e1) {
                            DialogUtil.showError(treeKeyStoreParent.getComponent(), e1.getLocalizedMessage());
                        }
                        AppManager.removeKeyStore(ksInfo.getPath());
                        treeKeyStoreParent.removeNode(node);
                    }

                    break;
                case CHANGE_PWD:
                    treeKeyStoreParent.changePassword(node, false);
                    break;
                default:
                    break;
            }

        }

    }

    private void fireKeystoreListChanged() {
        for (EventKeystoreListener eventKeystoreListener : keystoreListenerList){
            eventKeystoreListener.KeystoreAdded(null);
        }
    }

    public void addKeystoreListener(EventKeystoreListener listener){
        keystoreListenerList.add(listener);
    }

}
