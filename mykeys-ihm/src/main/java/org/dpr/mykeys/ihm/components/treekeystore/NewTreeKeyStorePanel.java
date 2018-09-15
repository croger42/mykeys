package org.dpr.mykeys.ihm.components.treekeystore;

/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Sun Microsystems nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.*;
import org.dpr.mykeys.app.PkiTools.TypeObject;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.*;
import org.dpr.mykeys.app.profile.ProfilStoreInfo;
import org.dpr.mykeys.ihm.actions.TreePopupMenu;
import org.dpr.mykeys.ihm.components.CertificateListPanel;
import org.dpr.mykeys.ihm.components.DetailPanel;
import org.dpr.mykeys.ihm.model.TreeKeyStoreModelListener;
import org.dpr.mykeys.ihm.model.TreeModel;
import org.dpr.mykeys.ihm.windows.certificate.CertificateCreateFactory;
import org.dpr.mykeys.ihm.windows.certificate.ImportCertificateDialog;
import org.dpr.mykeys.ihm.windows.certificate.SuperCreate;
import org.dpr.mykeys.keystore.ChangePasswordDialog;
import org.dpr.mykeys.utils.DialogUtil;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.*;
import java.util.List;

public class NewTreeKeyStorePanel extends JPanel implements MouseListener,
        TreeExpansionListener, TreeWillExpandListener, DropTargetListener, TreeKeyStoreActions {

    private final static Log log = LogFactory.getLog(NewTreeKeyStorePanel.class);
    private DetailPanel detailPanel;
    private CertificateListPanel listePanel;
    private GradientTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode acNode;
    private TreeModel treeModel;
    //
    // DefaultMutableTreeNode crlNode;
    //
    // DefaultMutableTreeNode sandBoxNode;
    private TreePopupMenu popup;

    public NewTreeKeyStorePanel(Dimension dim) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Create the nodes.
        rootNode = new DefaultMutableTreeNode("Magasins");
        acNode = new DefaultMutableTreeNode(Messages.getString(
                "store.ac.name"));

        treeModel = new TreeModel(rootNode);
        treeModel.addTreeModelListener(new TreeKeyStoreModelListener());

        tree = new GradientTree(treeModel);
        log.trace(tree.getUI());

        GradientTreeRenderer renderer = new GradientTreeRenderer();

        tree.setCellRenderer(renderer);
        renderer.jtree1 = tree;
        ToolTipManager.sharedInstance().registerComponent(tree);
        // javax.swing.ToolTipManager.ToolTipManager.sharedInstance().registerComponent(tree);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);

        popup = new TreePopupMenu("Popup name", this);

        treeModel.insertNodeInto(acNode, rootNode, rootNode.getChildCount());

        tree.setRootVisible(false);

        tree.addMouseListener(this);
        tree.addTreeWillExpandListener(this);
        tree.addTreeExpansionListener(this);
        // drop enabled
        tree.setDropMode(DropMode.ON);
        tree.setTransferHandler(new TreeTransferHandler());
        // Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        JPanel leftPanel = new JPanel();
        listePanel = new CertificateListPanel();
        JSplitPane splitLeftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Create the viewing pane.
        detailPanel = new DetailPanel();

        JScrollPane scrollDetail = new JScrollPane(detailPanel);
        scrollDetail.getVerticalScrollBar().setUnitIncrement(16);
        splitLeftPanel.setBottomComponent(scrollDetail);
        splitLeftPanel.setTopComponent(listePanel);
        splitLeftPanel.setDividerLocation(150);
        // Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(splitLeftPanel);
        splitPane.setDividerLocation(210);

        // Add the split pane to this panel.
        add(splitPane);

    }


    public static Map<String, String> getListCerts(String path, String type,
                                                   String password) throws KeyToolsException, KeyStoreException, ServiceException {
        KeyTools kt = new KeyTools();
        KeyStore ks = null;
        KeyStoreHelper ksv = new KeyStoreHelper(null);
        ks = ksv.loadKeyStore(path, StoreFormat.fromValue(type), password.toCharArray()).getKeystore();
        Map<String, String> certsAC = new HashMap<>();
        Enumeration<String> enumKs = ks.aliases();
        while (enumKs.hasMoreElements()) {
            String alias = enumKs.nextElement();
            Certificate cert = ks.getCertificate(alias);


            CertificateValue certInfo = ksv.fillCertInfo(ks, alias);

            certsAC.put(alias, alias);

        }

        return certsAC;

    }

    private void displayCertDetail(CertificateValue info) {
        detailPanel.updateInfo(info);

    }

    /**
     * .
     * <p>
     * <BR>
     *
     * @param info
     * @throws ServiceException
     */
    private void displayKeystoreList(NodeInfo info) throws ServiceException {
        listePanel.updateInfo(info);

    }

    /**
     * Update nodes with keystores list
     *
     * @param ksList
     * @throws KeyStoreException
     */
    public void updateKSList(HashMap<String, KeyStoreValue> ksList) throws KeyStoreException {
        clear();
        // Set<String> dirs = ksList.keySet();
        SortedSet<String> dirs = new TreeSet<>(
                String.CASE_INSENSITIVE_ORDER);
        dirs.addAll(ksList.keySet());
        addInternalKS();

        for (String dir : dirs) {
            KeyStoreValue ksinfo = ksList.get(dir);
            DefaultMutableTreeNode node = null;

            node = addObject(acNode, ksinfo, true);
        }

    }

    private void addInternalKS() throws KeyStoreException {
        DefaultMutableTreeNode nodei = addObject(acNode,
                KSConfig.getInternalKeystores().getStoreAC(), true);

    }

    /**
     * Remove all nodes except the root node.
     */
    private void clear() {
        acNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Remove the currently selected node.
     */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
                    .getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

    }

    public void removeNode(DefaultMutableTreeNode node) {

        MutableTreeNode parent = (MutableTreeNode) (node.getParent());
        if (parent != null) {
            treeModel.removeNodeFromParent(node);
            return;
        }

    }

    private DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                             Object child, boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        if (parent == null) {
            parent = rootNode;
        }

        // It is key to invoke this on the TreeModel, and NOT
        // DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }

        return childNode;
    }

    private void removeChildrenObjects(DefaultMutableTreeNode parent) {

        while (treeModel.getChildCount(parent) != 0) {
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) treeModel
                    .getChild(parent, 0));
        }

    }

    public boolean closeStore(DefaultMutableTreeNode node, boolean collapse) {
        KeyStoreValue ksInfo = ((KeyStoreValue) node.getUserObject());
        removeChildrenObjects(node);
        addObject(node, "[Vide]", false);
        if (collapse) {
            ksInfo.setOpen(false);
            tree.collapsePath(new TreePath(node.getPath()));
        }
        return true;

    }

    public boolean openStore(DefaultMutableTreeNode node,
                             boolean useInternalPwd, boolean expand) {
        KeyStoreValue ksInfo = ((KeyStoreValue) node.getUserObject());
        if (ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) { // equals(StoreModel.CASTORE))
            // {
            useInternalPwd = true;
        }
        // ask for password
        if (!useInternalPwd) {
            char[] password = DialogUtil.showPasswordDialog(this);

            if (password == null || password.length == 0) {
                return false;
            }

            ksInfo.setPassword(password);

        }
        KeyStoreHelper ksBuilder = new KeyStoreHelper(ksInfo);

        try {
            ksBuilder.loadKeyStore(ksInfo.getPath(), ksInfo.getStoreFormat(),
                    ksInfo.getPassword());
            ksInfo.setOpen(true);
        } catch (Exception e1) {
            DialogUtil.showError(NewTreeKeyStorePanel.this, e1.getMessage());
            log.error("load keystore failed", e1);
            return false;
        }

        // if (expand) {
        // ksInfo.setOpen(true);
        // tree.expandPath(new TreePath(node.getPath()));
        // }
        return true;

    }

    private void showPopupMenu(MouseEvent e) {
        DefaultMutableTreeNode tNode = null;
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath != null) {
            tNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        }

        popup.setNode(tNode);
        popup.show(tree, e.getX(), e.getY());

    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        // log.trace("collaps");

    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        // log.trace("expand");

    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {
        // log.trace("collapse1");

    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event)
            throws ExpandVetoException {
        // log.trace("ask expand");
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) event.getPath()
                .getLastPathComponent();
        if (tNode.getParent() != null) {
            Object object = tNode.getUserObject();
            if (object instanceof KeyStoreValue) {
                if (((KeyStoreValue) object).isOpen()) {
                    return;
                } else {

                    if (openStore(tNode, false, true)) {
                        return;
                    }

                }
            } else if (object instanceof String) {
                return;
            }
            throw new ExpandVetoException(event);

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int selRow = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                TreePath currentSelection = tree.getSelectionPath();
                if (currentSelection == null
                        || !currentSelection.equals(selPath)) {
                    tree.setSelectionPath(selPath);
                }

            }
            showPopupMenu(e);

        }
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
            if (e.getClickCount() == 1) {
                DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) selPath
                        .getLastPathComponent();
                Object object = tNode.getUserObject();
                if (object instanceof CertificateValue) {
                    CertificateValue certInfo = ((CertificateValue) object);
                    displayCertDetail(certInfo);

                } else {
                    displayCertDetail(null);
                    if (object instanceof KeyStoreValue) {
                        KeyStoreValue ksiInfo = ((KeyStoreValue) object);
                        if (ksiInfo != null)
                            try {
                                displayKeystoreList(ksiInfo);
                            } catch (ServiceException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                    } else if (object instanceof ProfilStoreInfo) {
                        ProfilStoreInfo ksiInfo = ((ProfilStoreInfo) object);
                        if (ksiInfo != null)
                            try {
                                displayKeystoreList(ksiInfo);
                            } catch (ServiceException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                    } else {
                        try {
                            displayKeystoreList(null);
                        } catch (ServiceException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }

                log.trace(selPath);
            } else if (e.getClickCount() == 2) {
                // DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)
                // selPath
                // .getLastPathComponent();
                // Object object = tNode.getUserObject();
                // if (object instanceof KeyStoreValue) {
                // KeyStoreValue ksInfo = ((KeyStoreValue) object);
                //
                // if (!ksInfo.isOpen()) {
                // openStore(tNode, false, true);
                //
                // }
                // }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (e.isPopupTrigger()) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {

                    TreePath currentSelection = tree.getSelectionPath();
                    if (currentSelection == null
                            || !currentSelection.equals(selPath)) {
                        tree.setSelectionPath(selPath);
                    }

                }
                showPopupMenu(e);

            }
            showPopupMenu(e);
        }

    }

    public void addCertificate(DefaultMutableTreeNode node, boolean b) throws ServiceException {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        SuperCreate cs = CertificateCreateFactory.getCreateDialog(frame, ksInfo,
                true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        openStore(node, true, true);
        displayKeystoreList(ksInfo);
        return;

    }

    public void importCertificate(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        ImportCertificateDialog cs = new ImportCertificateDialog(frame, ksInfo,
                true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        openStore(node, true, true);

    }

    public void changePassword(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        ChangePasswordDialog cs = new ChangePasswordDialog(frame, ksInfo);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);


    }

    @Override
    public void registerListener(EventListener listener) {

    }

    /**
     * .
     * <p>
     * <BR>
     *
     * @param node
     * @param b
     */
    public void exporterCertificate(DefaultMutableTreeNode node, boolean b) {
        log.error("Method removed !");
//        JFrame frame = (JFrame) tree.getTopLevelAncestor();
//        // KeyStoreValue ksInfo = null;
//        CertificateValue certInfo = null;
//        Object object = node.getUserObject();
//        if (object instanceof CertificateValue) {
//            certInfo = ((CertificateValue) object);
//        }
//        KeyStoreValue ksInfo = null;
//        DefaultMutableTreeNode objectKs = (DefaultMutableTreeNode) node
//                .getParent();// .getUserObject();
//        if (objectKs.getUserObject() instanceof KeyStoreValue) {
//            ksInfo = ((KeyStoreValue) objectKs.getUserObject());
//        }
//        ExportCertificateDialog cs = new ExportCertificateDialog(frame, ksInfo,
//                certInfo, true);
//        cs.setLocationRelativeTo(frame);
//        cs.setResizable(false);
//        cs.setVisible(true);

    }

    /**
     * .
     * <p>
     * <BR>
     *
     * @param node
     * @param b
     */
    public void addCertificateAC(DefaultMutableTreeNode node, boolean b) {
        JFrame frame = (JFrame) tree.getTopLevelAncestor();
        KeyStoreValue ksInfo = null;
        Object object = node.getUserObject();
        if (object instanceof KeyStoreValue) {
            ksInfo = ((KeyStoreValue) object);
        }
        SuperCreate cs = CertificateCreateFactory.getCreateDialog(frame, ksInfo,
                true);
        cs.setLocationRelativeTo(frame);
        cs.setResizable(false);
        cs.setVisible(true);
        openStore(node, true, true);

        return;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        boolean isActionCopy = false;
        System.out.println("drop");
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            if ((dtde.getDropAction() & DnDConstants.ACTION_COPY) != 0) {
                isActionCopy = true;
            }
            // Accept the drop and get the transfer data
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = false;
                List fileList = (List) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                File transferFile = (File) fileList.get(0);
                TypeObject typeObject = PkiTools.getTypeObject(transferFile);
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor) && typeObject != TypeObject.UNKNOWN && typeObject != null) {

                    result = dropFile(transferable, isActionCopy);
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

    private boolean dropFile(Transferable transferable, boolean isActionCopy) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        System.out.println("dropevent");

    }

    class TreeTransferHandler extends TransferHandler {
        DataFlavor nodesFlavor;
        DataFlavor[] flavors = new DataFlavor[1];

        /**
         * .
         * <p>
         * <BR><pre>
         * <b>Algorithme : </b>
         * DEBUT
         * <p>
         * FIN</pre>
         *
         * @param arg0
         * @return
         * @see TransferHandler#importData(TransferSupport)
         */
        @Override
        public boolean importData(TransferSupport arg0) {
            // TODO Auto-generated method stub
            return super.importData(arg0);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.TransferHandler#canImport(javax.swing.TransferHandler
         * .TransferSupport)
         */
        @Override
        public boolean canImport(TransferSupport support) {

            if (!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            //log.trace(nodesFlavor.getHumanPresentableName());
            if (!support.isDataFlavorSupported(nodesFlavor)) {
                //return false;
            }
            return true;
        }

    }

    class PopupHandler implements ActionListener {
        JTree tree;

        JPopupMenu popup;

        Point loc;

        public PopupHandler(JTree tree, JPopupMenu popup) {
            this.tree = tree;
            this.popup = popup;
            // tree.addMouseListener(ma);
        }

        public void actionPerformed(ActionEvent e) {
            log.trace("popuprr");
            String ac = e.getActionCommand();
            TreePath path = tree.getPathForLocation(loc.x, loc.y);
            // //log.trace("path = " + path);
            // //System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
            // if(ac.equals("ADD CHILD"))
            // log.trace("popuprr");
            // if(ac.equals("ADD SIBLING"))
            // addSibling(path);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

}