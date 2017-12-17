package org.dpr.swingutils;

import org.dpr.mykeys.ihm.windows.ComponentType;

public class PanelBuilder {

	LabelValuePanel lvPanel;

	public PanelBuilder() {
		super();
		lvPanel = new LabelValuePanel();
	}

	public PanelBuilder addComponent(String label, String key) {
		lvPanel.put(label, key, "");
		return this;
	}

	public void addComponent(String label, String key, ComponentType componentType) {
		lvPanel.put(label, componentType.getValue(), key,  "", true);
	}

	public void addEmptyLine() {
		lvPanel.putEmptyLine();
	}

	public LabelValuePanel toPanel() {
		// TODO Auto-generated method stub
		return lvPanel;
	}

	public void addComponent(String label, String key, String value) {
		lvPanel.put(label, key,value);
		
	}

}