package org.reldb.dbrowser.ui.content.rel.var;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelOutput;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class ExpressionResultViewerTab extends DbTreeTab {
	private CmdPanelOutput cmdPanel;
	private FilterSorter filterSorter;

	public ExpressionResultViewerTab(RelPanel parent, DbTreeItem item, FilterSorter filterSorter) {
		super(parent, item);
		
		Composite displayPanel = new Composite(parent.getTabFolder(), SWT.NONE) {
			public boolean setFocus() {
				return cmdPanel.setFocus();
			}
		};

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		displayPanel.setLayout(gridLayout);

		if (filterSorter == null)
			this.filterSorter = new FilterSorter(displayPanel, SWT.BORDER, item.getName(), parent.getConnection());
		else {
			this.filterSorter = filterSorter;
			filterSorter.clearListeners();
			filterSorter.setParent(displayPanel);
		}
		this.filterSorter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.filterSorter.addUpdateListener(source -> {
			evaluate();
			cmdPanel.setFocus();
		});
		
		try {
			cmdPanel = new CmdPanelOutput(displayPanel, parent.getConnection(), CmdPanelOutput.SHOW_FOR_EVALUATION_ONLY);
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			System.out.println("Error in ExpressionResultViewer: Unable to launch viewer due to: " + e);
			return;
		}
		cmdPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setControl(displayPanel);
		
		ready();
		evaluate();
	}
	
	private void evaluate() {
		cmdPanel.go(filterSorter.getQuery());
	}

	public ToolBar getToolBar(Composite parent) {
		return (new CmdPanelToolbar(parent, cmdPanel) {
			protected void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {
				super.addAdditionalItemsAfter(toolbar);
				new CommandActivator(Commands.Do.Refresh, this, "arrow_refresh", SWT.PUSH, "Refresh", e -> evaluate());
			}
		});
	}
	
	public FilterSorter getFilterSorter() {
		return filterSorter;
	}
	
}
