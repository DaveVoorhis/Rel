package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.reldb.dbrowser.ui.monitors.CheckForUpdates;
import org.reldb.dbrowser.ui.monitors.FreeCPUDisplay;
import org.reldb.dbrowser.ui.monitors.FreeMemoryDisplay;

public class StatusPanel extends Composite {
	private Label lblStatus;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StatusPanel(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 2;
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		
		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblStatus.setText("Ok");
		
		CheckForUpdates updateCheck = new CheckForUpdates(this, SWT.NONE);
		updateCheck.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true, 1, 1));
		
		FreeCPUDisplay freeCPU = new FreeCPUDisplay(this, SWT.BORDER);
		freeCPU.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		
		FreeMemoryDisplay freeRAM = new FreeMemoryDisplay(this, SWT.BORDER);
		freeRAM.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
	}

	public void setStatus(String s) {
		lblStatus.setText(s);
	}
}
