package org.reldb.relui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.ResourceManager;

public class TabPanel extends CTabItem {

	private Composite area;
	
	public TabPanel(CTabFolder parent, int style) {
		super(parent, style);
		
		area = new Composite(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		area.setLayout(gridLayout);
		
		TopPanel topPanel = new TopPanel(area, SWT.NONE);
		GridData gd_topPanel = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		topPanel.setLayoutData(gd_topPanel);
		
		setControl(area);
		
		setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
	}

	public Composite getContentParent() {
		return area;
	}

	public void setContent(Control comp) {
		GridData centre = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		comp.setLayoutData(centre);
	}
	
}
