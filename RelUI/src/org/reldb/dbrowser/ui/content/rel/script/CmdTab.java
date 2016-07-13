package org.reldb.dbrowser.ui.content.rel.script;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.RevDatabase;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdTab extends DbTreeTab {
	private CmdPanel cmdPanel;
	private RevDatabase database;
	private String name;
	
	public CmdTab(RelPanel parent, DbTreeItem item, int revstyle) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanel(parent.getConnection(), parent.getTabFolder(), CmdPanel.NONE);
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			System.out.println("Error: unable to launch command-line panel: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	    setControl(cmdPanel);
	    name = item.getName();
	    database = new RevDatabase(relPanel.getConnection());
	    cmdPanel.setContent(database.getScript(name));
	    ready();
	}
	
	public void dispose() {
		database.setScript(name, cmdPanel.getContent());
		super.dispose();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, cmdPanel.getCmdPanelOutput()).getToolBar();
	}
	
}
