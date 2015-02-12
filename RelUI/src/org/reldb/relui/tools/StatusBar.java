package org.reldb.relui.tools;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class StatusBar {
	
	@Inject
	public StatusBar() {
		
	}
	
    @PostConstruct
    public void createControls(final Composite parent) {
        final Group g = new Group(parent, SWT.SHADOW_ETCHED_IN | SWT.FILL);
        g.setLayout(new FillLayout());
    }
    
}