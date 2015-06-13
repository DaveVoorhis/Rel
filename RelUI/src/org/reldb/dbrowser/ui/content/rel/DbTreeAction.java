package org.reldb.dbrowser.ui.content.rel;

public abstract class DbTreeAction {
	
	protected RelPanel relPanel;
	
	public DbTreeAction(RelPanel relPanel) {
		this.relPanel = relPanel;
	}
	
	public abstract void go(DbTreeItem item);
}
