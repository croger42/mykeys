package org.dpr.mykeys.ihm.certificate;

import org.dpr.mykeys.ihm.Messages;
import org.dpr.mykeys.app.certificate.Certificate;
import org.dpr.mykeys.ihm.crl.CRLEditorDialog;
import org.dpr.swingtools.components.LabelValuePanel;

import javax.swing.*;
import java.awt.*;

public class CertificateCADetailPanel extends CertificateDetailPanel {
    public CertificateCADetailPanel(Certificate info) {
        super(info);
    }

    @Override
    protected void addCrlPanel(LabelValuePanel infosPanel) {

        JButton jbCreate = new JButton(Messages.getString("edit"));
        jbCreate.addActionListener(e -> {

            CRLEditorDialog cs = new CRLEditorDialog(info);

            cs.pack();
            cs.setVisible(true);

        });

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // jpDirectory.add(jl4);
//        jpDirectory.add(new JLabel("xxxxxxxx"));
        jpDirectory.add(jbCreate);


        infosPanel.put(Messages.getString("store.crl.name"),
                jpDirectory, true);
    }
}
