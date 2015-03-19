package org.reldb.relui.dbui;

import org.reldb.relui.tools.ModeTabContent;

public abstract class DbTabContent implements ModeTabContent {

	private DbTab parentTab;

	public DbTabContent(DbTab parentTab) {
		this.parentTab = parentTab;
	}
	
	public DbTab getDbTab() {
		return parentTab;
	}

}
