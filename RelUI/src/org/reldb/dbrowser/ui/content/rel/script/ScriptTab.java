package org.reldb.dbrowser.ui.content.rel.script;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.RevDatabase.Script;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class ScriptTab extends DbTreeTab {
	private CmdPanel cmdPanel;
	private RevDatabase database;
	private String name;
	private String oldScript;
	
	private boolean noreload = false;
	
	public ScriptTab(RelPanel parent, DbTreeItem item, int revstyle) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanel(parent.getConnection(), parent.getTabFolder(), CmdPanel.NONE) {
				@Override
				protected void notifyHistoryAdded(String historyItem) {
					database.addScriptHistory(name, historyItem);
					oldScript = historyItem;
				}
				@Override
				public void notifyExecuteSuccess() {
					getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							noreload = true;
							parent.redisplayed();
							noreload = false;
						}
					});
				}
				@Override
				protected String getDefaultSaveFileName() {
					return name;
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
	
	public void reload() {
		if (noreload)
			return;
		String newScript = cmdPanel.getInputText();
		if (!oldScript.equals(newScript))
			database.addScriptHistory(name, oldScript);
		Script script = database.getScript(name);
		oldScript = script.getContent();
		cmdPanel.setContent(script);
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
	
	@Override
	public boolean isSelfZoomable() {
		return true;
	}
	
	@Override
	public void zoom() {
		cmdPanel.zoom();
	}
	
}
