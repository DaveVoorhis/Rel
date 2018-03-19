package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorterState;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;

public class VarEditorTab extends DbTreeTab {
	
	private RelvarEditor relvarEditor;
	private FilterSorter filterSorter;
	
	public VarEditorTab(RelPanel parent, DbTreeItem item, FilterSorterState state) {
		super(parent, item);
		
		Composite displayPanel = new Composite(parent.getTabFolder(), SWT.NONE) {
			public boolean setFocus() {
				return relvarEditor.getControl().setFocus();
			}
		};
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		displayPanel.setLayout(gridLayout);
		
		filterSorter = new FilterSorter(displayPanel, SWT.BORDER, item.getName());
		filterSorter.setState(state);
		filterSorter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		filterSorter.addUpdateListener(source -> {
			relvarEditor.refresh();
			ready();
		});
		
		relvarEditor = new RelvarEditor(displayPanel, parent.getConnection(), filterSorter);
		relvarEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setControl(displayPanel);
		ready();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return new VarEditorToolbar(parent, relvarEditor).getToolBar();
	}
	
	public FilterSorterState getFilterSorterState() {
		return filterSorter.getState();
	}
	
}