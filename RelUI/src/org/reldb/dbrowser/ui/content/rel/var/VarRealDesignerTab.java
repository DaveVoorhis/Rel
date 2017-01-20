package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarDesignerComposite;

public class VarRealDesignerTab extends DbTreeTab {
	
	private RelvarDesignerComposite relvarDesigner;
	
	public VarRealDesignerTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		relvarDesigner = new RelvarDesignerComposite(parent.getTabFolder(), parent.getConnection(), item.getName());
		setControl(relvarDesigner);
		ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new VarRealDesignerToolbar(parent, relvarDesigner.getRelvarDesigner()).getToolBar();
	}
	
}