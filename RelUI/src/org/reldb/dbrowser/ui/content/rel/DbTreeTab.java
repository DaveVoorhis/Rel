package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class DbTreeTab extends CTabItem {
	
	protected RelPanel relPanel;
	protected DbTreeItem dbTreeItem;
	
	public DbTreeTab(RelPanel parent, DbTreeItem dbTreeItem) {
		super(parent.getTabFolder(), SWT.NONE);
		this.relPanel = parent;
		this.dbTreeItem = dbTreeItem;
		setText(dbTreeItem.getTabName());
		relPanel.notifyTabCreated();
	}

	public void reload() {}
	
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

	public boolean isSelfZoomable() {
		return false;
	}

	public void zoom() {}

	/** Override to allow a tab to reject a request to close. */
	public boolean canClose() {
		return true;
	}
}
