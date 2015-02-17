package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;

public class MainPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		ToolPanel toolPanel = new ToolPanel(this, SWT.BORDER);
		toolPanel.setLayoutData(BorderLayout.NORTH);
		
		Browser browser = new Browser(this, SWT.BORDER);
		toolPanel.setLayoutData(BorderLayout.CENTER);
		
		StatusPanel statusPanel = new StatusPanel(this, SWT.NONE);
		statusPanel.setLayoutData(BorderLayout.SOUTH);
		
		pack();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
