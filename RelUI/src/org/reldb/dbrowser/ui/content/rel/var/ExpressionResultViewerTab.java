package org.reldb.dbrowser.ui.content.rel.var;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelOutput;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class ExpressionResultViewerTab extends DbTreeTab {
	private CmdPanelOutput cmdPanel;
	
	public ExpressionResultViewerTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanelOutput(parent.getTabFolder(), parent.getConnection(), 0);
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			System.out.println("Error in ExpressionResultViewer: Unable to launch viewer due to: " + e);
			return;
		}
	    setControl(cmdPanel);
	    ready();
	    cmdPanel.go(item.getName());
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new CmdPanelToolbar(parent, cmdPanel).getToolBar();
	}
	
}
