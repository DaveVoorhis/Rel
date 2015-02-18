package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ToolPane {

	private CBanner banner;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPane(Composite parent, int style) {		
		banner = new CBanner(parent, SWT.BORDER);
		
		ToolBar leftBar = new ToolBar(banner, SWT.NONE);
		for (int i=0; i<8; i++) {
			ToolItem item = new ToolItem(leftBar, SWT.PUSH);
			item.setText ("Item " + i);
		}
		
		ToolBar rightBar = new ToolBar(banner, SWT.NONE);
		ToolItem rel = new ToolItem(rightBar, SWT.PUSH);
		rel.setText("Rel");
		ToolItem rev = new ToolItem(rightBar, SWT.PUSH);
		rev.setText("Rev");
		ToolItem cmd = new ToolItem(rightBar, SWT.PUSH);
		cmd.setText("Cmd");
		
		banner.setLeft(leftBar);
		banner.setRight(rightBar);
		
		banner.setLocation(0, 0);
		
		banner.pack();
	}

	public void setLayoutData(Object ld) {
		banner.setLayoutData(ld);
	}
	
}
