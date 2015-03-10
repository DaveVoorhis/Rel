package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTab;
import org.reldb.relui.tools.ModeTabContent;
import org.reldb.relui.tools.TopPanel;

public class DbTab extends ModeTab {
	
	public void addMode(String iconImageFilename, String toolTipText, ModeTabContent content) {
		addMode(ResourceManager.getPluginImage("RelUI", "icons/" + iconImageFilename), toolTipText, content);
	}
	
	public DbTab() {
		super(DbMain.getMainPanel().getTabFolder(), SWT.None);
		
		addMode("ModeRelIcon.png", "Rel", new DbTabContentRel());
		addMode("ModeRevIcon.png", "Rev", new DbTabContentRev());
		addMode("ModeCmdIcon.png", "Command line", new DbTabContentCmd());

		setMode(0);
		setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
	}
	
	public void buildLocationPanel(TopPanel parent) {
		new LocationPanel(parent, SWT.None);
	}

}
