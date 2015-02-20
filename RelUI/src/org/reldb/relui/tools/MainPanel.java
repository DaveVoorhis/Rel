package org.reldb.relui.tools;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

public class MainPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		CTabFolder tabFolder = new CTabFolder(this, SWT.NONE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setLayoutData(BorderLayout.CENTER);

		TabPanel tab = new TabPanel(tabFolder, SWT.NONE);
		tab.setText("Default");
		tabFolder.setSelection(tab);
		
		DemoContent cntnt1 = new DemoContent(tab.getContentParent(), SWT.NONE);
		tab.setContent(cntnt1);
		
		CTabItem tbtmNewItem = new TabPanel(tabFolder, SWT.NONE);
		tbtmNewItem.setText("New");
		
		StatusPanel statusPanel = new StatusPanel(this, SWT.NONE);
		statusPanel.setLayoutData(BorderLayout.SOUTH);
		
		pack();
	}
}
