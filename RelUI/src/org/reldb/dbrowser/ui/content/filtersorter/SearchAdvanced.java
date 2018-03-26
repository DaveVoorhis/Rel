package org.reldb.dbrowser.ui.content.filtersorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SearchAdvanced extends Composite {
	
	public SearchAdvanced(FilterSorter filterSorter, Composite contentPanel) {
		super(contentPanel, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);		

		Label label = new Label(this, SWT.NONE);
		label.setText("Advanced search goes here.");
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		
		ToolItem clear = new ToolItem(toolBar, SWT.PUSH);
		clear.addListener(SWT.Selection, e -> {
			filterSorter.fireUpdate();
		});
		clear.setText("Clear");
		
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	}
	
	public String getQuery() {
		return "";
	}

	public void setState(String state) {
	}

	public String getState() {
		return "";
	}
	
}
