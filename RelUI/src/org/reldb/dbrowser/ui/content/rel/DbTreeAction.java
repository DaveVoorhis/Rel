package org.reldb.dbrowser.ui.content.rel;

@FunctionalInterface
public interface DbTreeAction {
	public void go(DbTreeItem item);
}
