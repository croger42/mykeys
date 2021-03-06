package org.dpr.mykeys.ihm.components.treekeystore;

import org.dpr.mykeys.app.KeyUsages;
import org.dpr.mykeys.app.utils.CertificateUtils;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.app.crl.CrlValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.StoreLocationType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

import static org.dpr.swingtools.ImageUtils.createImageIcon;


class GradientTreeRenderer extends DefaultTreeCellRenderer implements
        TreeCellRenderer {

    public JTree jtree1;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean isSelected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        final JLabel rc = (JLabel) super.getTreeCellRendererComponent(tree,
                value, isSelected, expanded, leaf, row, hasFocus);
        String tooltip = null;

        setOpenIcon(createImageIcon("/images/Go-down.png"));
        setClosedIcon(createImageIcon("/images/Go-previous.png"));

        Color colorb =  UIManager.getColor("Tree.textBackground");
        Color colorf = UIManager.getColor("Tree.textForeground");
        this.setBackgroundSelectionColor(Color.GRAY);
        //setBackgroundSelectionColor

        // setTextNonSelectionColor( Color.black);
        if (value instanceof DefaultMutableTreeNode) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof KeyStoreValue) {

                KeyStoreValue kInfo = (KeyStoreValue) node.getUserObject();
                if (!kInfo.getStoreType().equals(StoreLocationType.INTERNAL))
                    tooltip = kInfo.getPath();
                 if (kInfo.isOpen()) {
                     colorf = Color.cyan;
                 }
                ImageIcon icon = null;
                // setTextNonSelectionColor( Color.green);
                switch (kInfo.getStoreModel()) {
                    case CERTSTORE:
                        icon = createImageIcon("/images/keystoreblue.png");
                        // icon = createImageIcon("/images/keystoreblueo.png");
                        break;
                    case CASTORE:
                        // icon = ImageUtils.createImageIcon("/images/keystorered.png");
                        break;
                    default:
                        icon = createImageIcon("/images/keystoreblue.png");
                        break;
                }

                switch (kInfo.getStoreType()) {
                    case INTERNAL:
                        icon = createImageIcon("/images/keystorered.png");
                        break;
                    default:
                        break;
                }

                if (icon != null) {

                    setIcon(icon);

                }
//                if (isSelected) {
//                    rc.setForeground(colorb);
//                }
            } else if (node.getUserObject() instanceof CrlValue) {
                CrlValue cInfo = (CrlValue) node.getUserObject();
                tooltip = cInfo.getPath();
                // if (kInfo.isOpen()) {
                ImageIcon icon = null;

                icon = createImageIcon("/images/keystorered.png");

                if (icon != null) {

                    setIcon(icon);

                }
//                if (isSelected) {
//                    rc.setForeground(colorb);
//                }
            } else if (node.getUserObject() instanceof Certificate) {

                Certificate cInfo = (Certificate) node.getUserObject();
                rc.setText(getCertificateLabel(cInfo));
                tooltip = cInfo.getName();
                boolean[] usages = KeyUsages.keyUsageFromInt(cInfo.getKeyUsageInt());
                ImageIcon icon = null;
                if (usages[5] && usages[6])
                    icon = createImageIcon("/images/ca1.png");
                else
                    icon = createImageIcon("/images/certificatekey.png");

                if (icon != null) {

                    setIcon(icon);

                }
//                if (isSelected) {
//                    rc.setForeground(colorb);
//                }
            }
        }
        rc.setForeground(colorf);
       // System.out.println(colorf);
        if (tooltip != null) {
            this.setToolTipText(tooltip);
        }
        setOpaque(false);
        return rc;
    }

    /**
     * Label shown in the tree.
     *
     * @param cInfo
     * @return
     */
    protected String getCertificateLabel(Certificate cInfo) {
        return cInfo.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (jtree1.getPathForLocation(getX(), getY()).getPathCount() != 2) {
            super.paintComponent(g);
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();// jtree1.getBounds().width;//getWidth( );
        int h = getHeight();

        // Paint a gradient from top to bottom
        Color color1 = getBackground();
        Color color2 = color1.darker();
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);

        super.paintComponent(g);

    }

    // @Override
    // public Dimension getPreferredSize() {
    // Dimension size = super.getPreferredSize();
    // size.width = jtree1.getBounds().width;
    //
    // return size;
    // }
    //
    // @Override
    // public void setBounds(final int x, final int y, final int width, final
    // int height) {
    // super.setBounds(x, y, Math.min(jtree1.getWidth()- x, width), height);
    //
    // }

}
