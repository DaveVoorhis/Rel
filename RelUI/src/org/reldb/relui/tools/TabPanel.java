package org.reldb.relui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import swing2swt.layout.BorderLayout;

public class TabPanel extends CTabItem {

	private Composite area;
	
	public TabPanel(CTabFolder parent, int style) {
		super(parent, style);
		
		area = new Composite(parent, style);
		area.setLayout(new BorderLayout());

		TopPanel topPanel = new TopPanel(area, SWT.NONE);
		topPanel.setLayoutData(BorderLayout.NORTH);
		
		setControl(area);
	}

	public Composite getContentParent() {
		return area;
	}

	public void setContent(Composite comp) {
		comp.setLayoutData(BorderLayout.CENTER);
	}
	
}
