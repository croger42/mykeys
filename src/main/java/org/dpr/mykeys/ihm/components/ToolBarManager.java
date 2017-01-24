package org.dpr.mykeys.ihm.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;

import org.dpr.mykeys.app.NodeInfo;
import org.dpr.mykeys.certificate.CertificateInfo;
import org.dpr.mykeys.certificate.CertificateToolBar;
import org.dpr.mykeys.keystore.KeyStoreInfo;
import org.dpr.mykeys.profile.Profil;
import org.dpr.mykeys.profile.ProfileToolBar;

public class ToolBarManager {

	CertificateToolBar certToolbar;
	ProfileToolBar profToolBar;

	public <T extends ObjToolBar> T getInstance(NodeInfo info) {
		if (info instanceof KeyStoreInfo) {
			return (T) certToolbar;

		} else {
			return (T) profToolBar;
		}

	}
	
	public <T extends ObjToolBar> T getInvInstance(NodeInfo info) {
		if (info instanceof KeyStoreInfo) {
			return (T) profToolBar;

		} else {
			return (T) certToolbar;
			
		}

	}

	public List<ObjToolBar> getToolBars() {
		List<ObjToolBar> liste = new ArrayList<ObjToolBar>();
		liste.add(certToolbar);
		liste.add(profToolBar);
		return liste;
	}

	public ObjToolBar getInstance2(NodeInfo info) {
		if (info instanceof KeyStoreInfo) {
			if (certToolbar == null)
				return certToolbar;

		} else {
			return profToolBar;
		}
		return null;
	}

	public void init(String string, KeysAction actions, ListPanel listPanel) {
		certToolbar = new CertificateToolBar("", new KeysAction(listPanel, listPanel));
		profToolBar = new ProfileToolBar("", new KeysProfileAction(listPanel, listPanel));

	}

	public void removeListeners(NodeInfo info) {
		getInstance(info).removeListeners();

	}

	public void enableActions(NodeInfo info) {
		getInstance(info).enableActions();

	}

	public void enableListeners(NodeInfo info) {
		getInstance(info).enableListeners();

	}

	public void disableActions(NodeInfo info) {
		getInstance(info).disableActions();

	}

	public void setTitle(String name) {
		certToolbar.setTitle(name);
		profToolBar.setTitle(name);

	}

	public Component getInstance() {
		// TODO Auto-generated method stub
		return certToolbar;
	}

	public void show(NodeInfo info) {
		getInstance(info).setVisible(true);
		getInvInstance(info).setVisible(false);
		
	}

}