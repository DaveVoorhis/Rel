package org.reldb.relui.tools;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class StatusBar {
	
	@Inject
	public StatusBar() {		
	}
	
    @PostConstruct
    public void createControls(final Composite parent) {
    	parent.setLayout(new FillLayout());
    	
		new StatusPanel(parent, SWT.NONE) {
			public Point computeSize(int wHint, int hHint, boolean changed) {
				int width = parent.getParent().getParent().getParent().getBounds().width;
				return new Point(width, 40);
			}
		};
    }

}