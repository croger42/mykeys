package org.dpr.mykeys.ihm.windows;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.AuthenticationService;
import org.dpr.mykeys.app.MkSession;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.ServiceException;
import org.dpr.mykeys.ihm.components.ListPanel;
import org.dpr.mykeys.ihm.model.UserModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

public class ManageUserDialog extends JFrame {
    public static final Log log = LogFactory.getLog(ListPanel.class);
    AuthenticationService authService;
    private JTable table;
    private UserModel modele;

    public ManageUserDialog() throws IhmException, ServiceException {
        setTitle(Messages.getString(Messages.getString("users.title")));
        setPreferredSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        authService = new AuthenticationService();
        List<String> userList2 = new ArrayList<>();


        modele = new UserModel(authService.listUsers());

        table = new JTable(modele);
        table.setRowSelectionAllowed(true);


        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);
        JPanel jp1 = new JPanel();
        jp.add(new JScrollPane(table), CENTER);
        // jp.add(jp1);
        jp.add(getButtonPanel());
        init();
        pack();
    }

    public ManageUserDialog(JFrame owner) {

        super();


    }

    public static void main(String[] args) throws IhmException, ServiceException {
        //KSConfig.init(".myKeys25");
        ManageUserDialog notesJFrame = new ManageUserDialog();
        notesJFrame.setVisible(true);
    }

    private JPanel getButtonPanel() {
        DialogAction dAction = new DialogAction();
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.LEADING));
        JButton jbAdd = new JButton(Messages.getString("template.create.button"));
        jbAdd.addActionListener(dAction);
        jbAdd.setActionCommand("add");
        JButton JbDelete = new JButton(Messages.getString(Messages.getString("user.delete.button")));
        JbDelete.addActionListener(dAction);
        JbDelete.setActionCommand("delete");

        // jp.add(jbAdd);
        jp.add(JbDelete);
        return jp;
    }


    private void init() {

    }


    class DialogAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (command.equals("add")) {


            } else if (command.equals("delete")) {

                int row = table.getSelectedRow();
                CertificateValue p = modele.getValueAt(row);
                if (MkSession.user == null || MkSession.user.equals(p.getAlias()))
                    return;
                try {
                    authService.deleteUser(p.getAlias());
                    modele.setUsers((authService.listUsers()));
                } catch (ServiceException e) {
                    e.printStackTrace();
                }

                modele.fireTableDataChanged();


            }
        }
    }
}
