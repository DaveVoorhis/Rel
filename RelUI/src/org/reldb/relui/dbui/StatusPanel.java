package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.reldb.relui.widgets.monitors.FreeCPUDisplay;
import org.reldb.relui.widgets.monitors.FreeMemoryDisplay;

public class StatusPanel extends Composite {
	private FreeCPUDisplay freeCPU;
	private FreeMemoryDisplay freeRAM;
	private Label lblStatus;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StatusPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		lblStatus = new Label(this, SWT.NONE);
		FormData fd_lblStatus = new FormData();
		fd_lblStatus.top = new FormAttachment(0);
		fd_lblStatus.left = new FormAttachment(0);
		lblStatus.setLayoutData(fd_lblStatus);
		lblStatus.setText("Status");
		
		freeRAM = new FreeMemoryDisplay(this, SWT.BORDER);
		FormData fd_ram = new FormData();
		fd_ram.top = new FormAttachment(0);
		fd_ram.right = new FormAttachment(100);
		freeRAM.setLayoutData(fd_ram);
		
		freeCPU = new FreeCPUDisplay(this, SWT.BORDER);
		fd_lblStatus.right = new FormAttachment(freeCPU);
		FormData fd_cpu = new FormData();
		fd_cpu.top = new FormAttachment(0);
		fd_cpu.right = new FormAttachment(freeRAM);
		freeCPU.setLayoutData(fd_cpu);
	}

	public void setStatus(String s) {
		lblStatus.setText(s);
	}
}