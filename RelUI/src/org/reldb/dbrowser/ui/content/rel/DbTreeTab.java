package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class DbTreeTab extends CTabItem {
	
	private RelPanel relPanel;
	
	public DbTreeTab(RelPanel parent, DbTreeItem item) {
		super(parent.getTabFolder(), SWT.NONE);
		this.relPanel = parent;
		setText(item.getTabName());
	}

	public void ready() {
		relPanel.getTabFolder().setSelection(this);
		relPanel.fireDbTreeTabchangeEvent();
	}
	
	public void dispose() {
		super.dispose();
		relPanel.fireDbTreeTabchangeEvent();		
	}
	
	public ToolBar getToolBar(Composite parent) {
		return null;
	}
	
}
