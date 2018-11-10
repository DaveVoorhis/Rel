package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.graphics.Image;

public abstract class DbTreeAction {
	
	protected RelPanel relPanel;
	
	public DbTreeAction(RelPanel relPanel) {
		this.relPanel = relPanel;
	}
	
	public abstract void go(DbTreeItem item, Image image);
}
