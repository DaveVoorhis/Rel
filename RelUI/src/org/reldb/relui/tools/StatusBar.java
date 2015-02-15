package org.reldb.relui.tools;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.reldb.relui.monitors.FreeCPUDisplay;
import org.reldb.relui.monitors.FreeMemoryDisplay;

public class StatusBar {
	
	@Inject
	public StatusBar() {
		
	}
	
    @PostConstruct
    public void createControls(final Composite parent) {
		parent.setLayout(new FillLayout());

		Label blah1 = new Label(parent, SWT.BORDER);
		blah1.setText("Test1");
		
		Label blah2 = new Label(parent, SWT.BORDER);
		blah2.setText("Test2");
		
		new FreeCPUDisplay(parent, SWT.BORDER);
		new FreeMemoryDisplay(parent, SWT.BORDER);
    }
    
}