package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;

public class VarViewerTab extends DbTreeTab {
	
	private RelvarEditor relvarEditor;
	
	public VarViewerTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		// TODO replace this with command-line output display of relvar contents.
		System.out.println("VarViewerTab: replace this with command-line output display of relvar contents.");
		relvarEditor = new RelvarEditor(parent.getTabFolder(), parent.getConnection(), item.getName());
		setControl(relvarEditor.getControl());
		ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new VarEditorToolbar(parent, relvarEditor).getToolBar();
	}
	
}