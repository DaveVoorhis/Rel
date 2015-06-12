package org.reldb.dbrowser.ui.content.rel;

@FunctionalInterface
public interface DbTreeListener {
	public void select(DbTreeItem item);
}
