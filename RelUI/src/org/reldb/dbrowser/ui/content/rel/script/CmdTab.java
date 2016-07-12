package org.reldb.dbrowser.ui.content.rel.script;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdTab extends DbTreeTab {
//	private Rev rev;
	private CmdPanel cmdPanel;
	
	public CmdTab(RelPanel parent, DbTreeItem item, int revstyle) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanel(parent.getConnection(), parent.getTabFolder(), CmdPanel.NONE);
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
//	    rev = new Rev(parent.getTabFolder(), parent.getConnection(), parent.getCrashHandler(), item.getName(), revstyle);  
	    setControl(cmdPanel);
	    ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, cmdPanel.getCmdPanelOutput()).getToolBar();
	}
	
}
