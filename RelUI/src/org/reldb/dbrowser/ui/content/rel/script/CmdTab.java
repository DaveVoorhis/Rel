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
import org.reldb.dbrowser.ui.content.rev.RevDatabase.Script;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdTab extends DbTreeTab {
	private CmdPanel cmdPanel;
	private RevDatabase database;
	private String name;
	private String oldScript;
	
	public CmdTab(RelPanel parent, DbTreeItem item, int revstyle) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanel(parent.getConnection(), parent.getTabFolder(), CmdPanel.NONE) {
				@Override
				protected void notifyHistoryAdded(String historyItem) {
					database.addScriptHistory(name, historyItem);
					oldScript = historyItem;
				}
			};
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			System.out.println("Error: unable to launch command-line panel: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	    setControl(cmdPanel);
	    name = item.getName();
	    database = new RevDatabase(relPanel.getConnection());
	    Script script = database.getScript(name);
	    oldScript = script.getContent();
	    cmdPanel.setContent(script);
	    ready();
	}
	
	public void dispose() {
		String newScript = cmdPanel.getInputText();
		if (!oldScript.equals(newScript))
			database.addScriptHistory(name, oldScript);
		database.setScript(name, cmdPanel.getInputText());
		super.dispose();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, cmdPanel.getCmdPanelOutput()).getToolBar();
	}
	
}
