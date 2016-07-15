package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.ModelChangeListener;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class RevTab extends DbTreeTab {
	private Rev rev;
	
	public RevTab(RelPanel parent, DbTreeItem item, int revstyle) {
		super(parent, item);
	    rev = new Rev(parent.getTabFolder(), parent.getConnection(), parent.getCrashHandler(), item.getName(), revstyle);  
	    setControl(rev);
	    ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, rev.getCmdPanelOutput()).getToolBar();
	}

	public void addModelChangeListener(ModelChangeListener modelChangeListener) {
		rev.addModelChangeListener(modelChangeListener);
	}

	public int getRevStyle() {
		return rev.getRevStyle();
	}
	
	@Override
	public boolean isSelfZoomable() {
		return true;
	}
	
	@Override
	public void zoom() {
		rev.zoom();
	}
	
}
