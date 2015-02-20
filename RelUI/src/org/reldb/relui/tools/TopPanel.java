package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

public class TopPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public TopPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.VERTICAL));
		
		LocationPanel location = new LocationPanel(this, SWT.NONE);
		ToolPanel tools = new ToolPanel(this, SWT.NONE);
		
		pack();
	}

}
