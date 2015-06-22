package org.reldb.dbrowser.ui.content.rel.var;

import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class RelvarEditorTab extends DbTreeTab {
	
	public RelvarEditorTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		RelvarEditor relvarEditor = new RelvarEditor(parent.getTabFolder(), parent.getConnection(), item.getName());
		setControl(relvarEditor.getContents());
	}
	
}