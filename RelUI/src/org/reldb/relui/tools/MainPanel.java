package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;

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
		
		ToolPane toolPanel = new ToolPane(this, SWT.NONE);
		toolPanel.setLayoutData(BorderLayout.NORTH);
		
		ContentPanel content = new ContentPanel(this, SWT.BORDER);
		content.setLayoutData(BorderLayout.CENTER);
		
		StatusPanel statusPanel = new StatusPanel(this, SWT.NONE);
		statusPanel.setLayoutData(BorderLayout.SOUTH);
		
		pack();
	}
}
