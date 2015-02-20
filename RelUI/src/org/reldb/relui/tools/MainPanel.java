package org.reldb.relui.tools;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.SWT;

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

		TabPanel tabDefault = new TabPanel(tabFolder, SWT.NONE);
		tabDefault.setText("Default");
		tabDefault.setContent(new DemoContent(tabDefault.getContentParent(), SWT.NONE));
		
		TabPanel tabNew = new TabPanel(tabFolder, SWT.NONE);
		tabNew.setText("New");
		Label tabNewContent = new Label(tabNew.getContentParent(), SWT.BORDER);
		tabNewContent.setText("Nothing is here yet.");
		tabNew.setContent(tabNewContent);
		
		tabFolder.setSelection(tabDefault);
		
		StatusPanel statusPanel = new StatusPanel(this, SWT.NONE);
		statusPanel.setLayoutData(BorderLayout.SOUTH);
		
		pack();
	}
}
