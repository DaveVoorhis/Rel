package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTab;
import org.reldb.relui.tools.ModeTabContent;
import org.reldb.relui.tools.TopPanel;

public class DbTab extends ModeTab {
	
	private LocationPanel locationPanel;
	
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

	public void openDatabaseAtURI(String uri, boolean canCreate) {
		System.out.println("DbTab: attempt to open database at " + locationPanel.getDatabaseURI() + " with create==" + canCreate);
		if (countModes() == 0)
			new DbTab();
		setText(uri);
		setShowClose(true);
	}

	private void createDatabaseAtLocation() {
		openDatabaseAtURI(locationPanel.getDatabaseURI(), true);
	}
	
	private void openDatabaseAtLocation() {
		openDatabaseAtURI(locationPanel.getDatabaseURI(), false);
	}
	
	public void buildLocationPanel(TopPanel parent) {
		locationPanel = new LocationPanel(parent, SWT.None) {
			@Override
			public void notifyDatabaseURIModified() {
				openDatabaseAtLocation();
			}
		};
	}

	public void newDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI("db://file:" + string);
		createDatabaseAtLocation();
	}

	public void openLocalDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI("db://file:" + string);
		openDatabaseAtLocation();
	}

	public void openRemoteDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI(string);
		openDatabaseAtLocation();
	}

}
