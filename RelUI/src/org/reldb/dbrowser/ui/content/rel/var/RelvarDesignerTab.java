package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class RelvarDesignerTab extends DbTreeTab {
	
	private RelvarDesigner relvarDesigner;
	
	public RelvarDesignerTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		relvarDesigner = new RelvarDesigner(parent.getTabFolder(), parent.getConnection(), item.getName());
		setControl(relvarDesigner.getControl());
		ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new RelvarDesignerToolbar(parent, relvarDesigner).getToolBar();
	}
	
}