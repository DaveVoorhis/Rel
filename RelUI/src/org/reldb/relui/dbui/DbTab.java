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
		
		setImage(ResourceManager.getPluginImage("RelUI", "icons/plusIcon.png"));
		setToolTipText("New tab");
	}
	
	public void setText(String s) {
		super.setText(s);
		if (countModes() == 0) {
			setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
			setToolTipText(s);
			addMode("ModeRelIcon.png", "Rel", new DbTabContentRel());
			addMode("ModeRevIcon.png", "Rev", new DbTabContentRev());
			addMode("ModeCmdIcon.png", "Command line", new DbTabContentCmd());
			setMode(0);
		}
	}
	
	public void buildLocationPanel(TopPanel parent) {
		new LocationPanel(parent, SWT.None);
	}

}
