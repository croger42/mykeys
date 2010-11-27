package org.dpr.mykeys.ihm.menuaction;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.dpr.mykeys.ihm.KeyStoreUI;
import org.dpr.mykeys.ihm.windows.CreateStoreDialog;
import org.dpr.mykeys.ihm.windows.ImportStoreDialog;

public class MenuAction extends AbstractAction {

    private KeyStoreUI keyStoreUI;

    ResourceBundle messages = ResourceBundle.getBundle("org.dpr.mykeys.config.Messages",
	    Locale.getDefault());

    public MenuAction(Object keyStoreUI, String string) {
	super(string);
	this.keyStoreUI = (KeyStoreUI) keyStoreUI;
    }

    public MenuAction(Object keyStoreUI, String nomImg, ImageIcon ic) {
	super(nomImg, ic);
	this.keyStoreUI = (KeyStoreUI) keyStoreUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	final String action = e.getActionCommand();
	final Object composant = e.getSource();
	new Thread(new Runnable() {
	    public void run() {
		if (action.equals("newStore")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    CreateStoreDialog cs = new CreateStoreDialog(
				    keyStoreUI, true);
			    cs.setLocationRelativeTo(keyStoreUI);
			    cs.setVisible(true);
			}
		    });

		} else if (action.equals("loadStore")) {
		    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ImportStoreDialog cs = new ImportStoreDialog(
				    keyStoreUI, true);
			    cs.setLocationRelativeTo(keyStoreUI);

			    cs.setVisible(true);
			}
		    });

		}

	    }
	}).start();

    }

}
// javax.swing.SwingUtilities.invokeLater(new Runnable() {
// public void run() {
// createAndShowGUI();
// }
// });

// CreateStoreDialog cs = new CreateStoreDialog(keyStoreUI,
// true);
// cs.setLocationRelativeTo(keyStoreUI);
// cs.setVisible(true);