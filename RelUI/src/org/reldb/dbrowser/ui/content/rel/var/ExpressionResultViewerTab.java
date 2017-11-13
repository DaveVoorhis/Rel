package org.reldb.dbrowser.ui.content.rel.var;

import java.io.IOException;

import org.eclipse.swt.SWT;
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
	private String name;

	public ExpressionResultViewerTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		try {
			cmdPanel = new CmdPanelOutput(parent.getTabFolder(), parent.getConnection(),
					CmdPanelOutput.SHOW_FOR_EVALUATION_ONLY);
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			System.out.println("Error in ExpressionResultViewer: Unable to launch viewer due to: " + e);
			return;
		}
		setControl(cmdPanel);
		ready();
		name = item.getName();
		evaluate();
	}

	private void evaluate() {
		cmdPanel.go(name);
	}

	public ToolBar getToolBar(Composite parent) {
		return (new CmdPanelToolbar(parent, cmdPanel) {
			protected void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {
				addItem("Refresh", "arrow_refresh", SWT.PUSH).addListener(SWT.Selection, e -> evaluate());
			}
		}).getToolBar();
	}

}
