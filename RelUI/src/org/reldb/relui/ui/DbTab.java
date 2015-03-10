package org.reldb.relui.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTab;
import org.reldb.relui.tools.ModeTabContent;
import org.reldb.relui.tools.TopPanel;

public class DbTab extends ModeTab {
	
	public void addMode(String iconImageFilename, String toolTipText, ModeTabContent content) {
		addMode(ResourceManager.getPluginImage("RelUI", "icons/" + iconImageFilename), toolTipText, content);
	}
	
	public DbTab(CTabFolder parent, int style) {
		super(parent, style);
		
		addMode("ModeRelIcon.png", "Rel", new DbTabContentRel());
		addMode("ModeRevIcon.png", "Rev", new DbTabContentRev());
		addMode("ModeCmdIcon.png", "Command line", new DbTabContentCmd());

		setMode(0);
		setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
		setText("Default");
	}
	
	public void buildLocationPanel(TopPanel parent) {
		new LocationPanel(parent, SWT.None);
	}

}
