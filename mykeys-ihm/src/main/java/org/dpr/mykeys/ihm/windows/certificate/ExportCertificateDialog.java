package org.dpr.mykeys.ihm.windows.certificate;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpr.mykeys.Messages;
import org.dpr.mykeys.app.CommonServices;
import org.dpr.mykeys.app.KSConfig;
import org.dpr.mykeys.app.KeyTools;
import org.dpr.mykeys.app.MkSession;
import org.dpr.mykeys.app.certificate.CertificateValue;
import org.dpr.mykeys.app.keystore.KeyStoreValue;
import org.dpr.mykeys.app.keystore.KeyStoreHelper;
import org.dpr.mykeys.app.keystore.StoreFormat;
import org.dpr.mykeys.app.keystore.StoreLocationType;
import org.dpr.mykeys.utils.DialogUtil;
import org.dpr.swingtools.components.JFieldsPanel;
import org.dpr.swingtools.components.LabelValuePanel;

public class ExportCertificateDialog extends JDialog implements ItemListener
{

    private static final Log log = LogFactory
            .getLog(ExportCertificateDialog.class);
    private JTextField tfDirectory;

    public static final String PEM_KEY_EXT = ".key";

    public static final String PEM_CERT_EXT = ".cer";

    private LabelValuePanel infosPanel;

    private CertificateValue certInfo;

    private KeyStoreValue ksInfo;

    private boolean isExportCle = false;

    // Map<String, String> elements = new HashMap<String, String>();

    public ExportCertificateDialog(Frame owner, KeyStoreValue ksInfo,
                                   CertificateValue certInfo, boolean modal)
    {
        super(owner, modal);
        this.certInfo = certInfo;
        this.ksInfo = ksInfo;
        init();
        this.pack();
    }

    private void init()
    {
        DialogAction dAction = new DialogAction();
        setTitle(Messages.getString("dialog.export.title"));
        JPanel jp = new JPanel();
        BoxLayout bl = new BoxLayout(jp, BoxLayout.Y_AXIS);
        jp.setLayout(bl);
        setContentPane(jp);

        Map<String, String> mapType = new LinkedHashMap<>();
        mapType.put("der", "der");
        mapType.put("pem", "pem");
        mapType.put("pkcs12", "pkcs12");

        // mapType.put("der", "der");

        infosPanel = new LabelValuePanel();

        // infosPanel.put("Format", JComboBox.class, "formatCert", mapType);
        infosPanel.put("Format", ButtonGroup.class, "formatCert", mapType, "");
        // infosPanel.put("Export de la clé privée", JCheckBox, "");
        if (certInfo.isContainsPrivateKey())
        {

            infosPanel.put("Exporter la clé privée", JCheckBox.class,
                    "isExportKey", "true", true);

        }

        infosPanel.putEmptyLine();

        tfDirectory = new JTextField(35);

        File outputFile = getTargetFile(null);
        tfDirectory.setText(outputFile.getAbsolutePath());
        // FileSystemView fsv = FileSystemView.getFileSystemView();
        // File f = fsv.getDefaultDirectory();
        // tfDirectory.setText(f.getAbsolutePath());
        JButton jbChoose = new JButton("...");
        jbChoose.addActionListener(dAction);
        jbChoose.setActionCommand("CHOOSE_IN");

        JPanel jpDirectory = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // jpDirectory.add(jl4);
        jpDirectory.add(tfDirectory);
        jpDirectory.add(jbChoose);
        JButton jbOK = new JButton(Messages.getString("button.confirm"));
        jbOK.addActionListener(dAction);
        jbOK.setActionCommand("OK");
        JButton jbCancel = new JButton(Messages.getString("button.cancel"));
        jbCancel.addActionListener(dAction);
        jbCancel.setActionCommand("CANCEL");
        JFieldsPanel jf4 = new JFieldsPanel(jbOK, jbCancel, FlowLayout.RIGHT);

        infosPanel.put(Messages.getString("dialog.generic.fileout"),
                jpDirectory, true);
        jp.add(infosPanel);
        jp.add(jf4);

    }

    class DialogAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent event)
        {
            Map<String, Object> elements = infosPanel.getElements();
            String command = event.getActionCommand();
            if (command.equals("CHOOSE_IN"))
            {
                // if (StringUtils.isEmpty(tfDirectory.getText()){
                //
                // }
                String format = (String) infosPanel.getElements().get(
                        "formatCert");
                File outputFile = getTargetFile(format);

                JFileChooser jfc = new JFileChooser(outputFile);
                // the first, only, and selected filter is 'All Files'
                jfc.removeChoosableFileFilter(jfc.getFileFilter());
                //add filters
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers der (*.der)"));
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers PKCS12 (*.p12)"));
                jfc.addChoosableFileFilter(new KeyStoreFileFilter("der", "fichiers pem (*.pem)"));
                jfc.setSelectedFile(outputFile);
                // jPanel1.add(jfc);
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                {

                    //TODO: remove or not ?
                    KSConfig.getUserCfg().setProperty("output.path",
                            jfc.getSelectedFile().getParent());
                    tfDirectory
                            .setText(jfc.getSelectedFile().getAbsolutePath());

                }

            }
            else if (command.equals("OK"))
            {
                if (tfDirectory.getText().equals(""))
                {
                    DialogUtil.showError(ExportCertificateDialog.this,
                            "Champs invalides");
                    return;
                }

                String path = tfDirectory.getText();
                // saisie mot de passe
                char[] password = null;
                char[] privKeyPwd = null;
                Object o = infosPanel.getElements().get(
                        "isExportKey");
                boolean isExportCle = o==null?false:(Boolean) o;

                KeyTools kt = new KeyTools();
                System.out.println(MkSession.password);
                System.out.println(MkSession.user);
                KeyStoreHelper kServ = new KeyStoreHelper(ksInfo);
                String format = (String) infosPanel.getElements().get(
                        "formatCert");
                if (!ksInfo.getStoreType().equals(StoreLocationType.INTERNAL)) {
                    if (isExportCle) {
                        privKeyPwd = DialogUtil.showPasswordDialog(null, "mot de passe de la cl� priv�e");
                    }
                } else
                    privKeyPwd = MkSession.password;
                certInfo.setPassword(privKeyPwd);
                if (format.equalsIgnoreCase("pkcs12"))
                {
                    password = DialogUtil.showPasswordDialog(null, "mot de passe d'exportation");


                    CommonServices cact = new CommonServices();

                    try
                    {
                        cact.exportCert(ksInfo, StoreFormat.PKCS12, path,
                                password, certInfo, isExportCle, privKeyPwd);
                    }
                    catch (Exception e)
                    {
                        log.error(e);
                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());
                    }
                }
                else if (format.equals("der"))
                {
                    try
                    {
                        kt.exportDer(certInfo, path);
                        if (isExportCle)
                        {
                        	kServ.exportPrivateKey(certInfo, privKeyPwd,
                                    tfDirectory.getText());
                        }

                    }
                    catch (Exception e)
                    {

                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());

                    }

                }
                else
                {
                    try
                    {
                        kt.exportPem(certInfo, path);
                        if (isExportCle)
                        {
                        	kServ.exportPrivateKeyPEM(certInfo, ksInfo, privKeyPwd,
                                    tfDirectory.getText());
                        }

                    }
                    catch (Exception e)
                    {

                        DialogUtil.showError(ExportCertificateDialog.this,
                                e.getLocalizedMessage());

                    }
                }
                ExportCertificateDialog.this.setVisible(false);
                DialogUtil.showInfo(ExportCertificateDialog.this,
                        "Exportation terminée");
            }
            else if (command.equals("CANCEL"))
            {
                ExportCertificateDialog.this.setVisible(false);
            }

        }

    }

    /**
     * @author Christophe Roger
     * @date 8 mai 2009
     */
    class KeyStoreFileFilter extends FileFilter
    {

        private String filterExtension;
        private String filterDescription;

        private KeyStoreFileFilter(String extension, String descrip)
        {
            this.filterExtension = extension;
            this.filterDescription = descrip;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FilenameUtils.getExtension(f.getName());
            return extension != null && extension.equalsIgnoreCase(filterExtension);

        }


        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription()
        {
            // TODO Auto-generated method stub
            return filterDescription;
        }

    }

    public void updateKeyStoreList()
    {
        // TODO Auto-generated method stub

    }

    private File getTargetFile(String format)
    {
        File pathSrc = new File(KSConfig.getDataDir());
        if (pathSrc != null && !pathSrc.isDirectory())
        {
            pathSrc = new File(pathSrc.getParent());
        }
        String fileName = null;
        if (null == format) {
            return new File(pathSrc, certInfo.getAlias());
        }
        if (format.equalsIgnoreCase("pkcs12"))
        {
            fileName = certInfo.getAlias() + KeyTools.EXT_P12;
        }
        else if (format.equalsIgnoreCase("der"))
        {
            fileName = certInfo.getAlias() + KeyTools.EXT_DER;
        } else if (format.equalsIgnoreCase("pem")) {
            fileName = certInfo.getAlias() + KeyTools.EXT_PEM;
        } else {
            fileName = certInfo.getAlias();
        }
        return new File(pathSrc, fileName);


    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        Object source = e.getItemSelectable();
        JCheckBox jc = (JCheckBox) source;
        isExportCle = jc.isSelected();
        // for (int i=0; i<X509Constants.keyUsageLabel.length; i++){
        // if (val.equals(X509Constants.keyUsageLabel[i])){
        // certInfo.getKeyUsage()[i]=jc.isSelected();
        // return;
        // }
        // }

    }

    /**
     * Works around a JFileChooser limitation, that the selected file when saving
     * is returned exactly as typed and doesn't take into account the selected
     * file filter.
     */
    public static File getSelectedFileWithExtension(JFileChooser c)
    {
        File file = c.getSelectedFile();
        if (c.getFileFilter() instanceof FileNameExtensionFilter)
        {
            String[] exts = ((FileNameExtensionFilter) c.getFileFilter()).getExtensions();
            String nameLower = file.getName().toLowerCase();
            for (String ext : exts)
            { // check if it already has a valid extension
                if (nameLower.endsWith('.' + ext.toLowerCase()))
                {
                    return file; // if yes, return as-is
                }
            }
            // if not, append the first one from the selected filter
            file = new File(file.toString() + '.' + exts[0]);
        }
        return file;
    }
}
