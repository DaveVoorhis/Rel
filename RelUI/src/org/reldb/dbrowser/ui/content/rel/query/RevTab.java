package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class RevTab extends DbTreeTab {
	private Rev rev;
	
	public RevTab(RelPanel parent, String name) {
		super(parent);
	    rev = new Rev(parent.getTabFolder(), parent.getConnection(), parent.getCrashHandler(), name);  
	    setControl(rev);
	    ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, rev.getCmdPanelOutput()).getToolBar();
	}
}
