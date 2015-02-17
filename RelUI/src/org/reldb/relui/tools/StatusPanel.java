package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.reldb.relui.monitors.FreeCPUDisplay;
import org.reldb.relui.monitors.FreeMemoryDisplay;

public class StatusPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public StatusPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		Label lblStatus = new Label(this, SWT.NONE);
		lblStatus.setLayoutData(BorderLayout.WEST);
		lblStatus.setText("Status");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.EAST);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		FreeMemoryDisplay memDisplay = new FreeMemoryDisplay(composite, SWT.BORDER);
		FreeCPUDisplay cpuDisplay = new FreeCPUDisplay(composite, SWT.BORDER);
		
		pack();
	}

	private Point preferredSize() {
		return new Point(100, 25);
	}
	
	public Point computeSize(int w, int h) {
		return preferredSize(); 
	}

	public Point computeSize(int w, int h, boolean changed) {
		return preferredSize();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
