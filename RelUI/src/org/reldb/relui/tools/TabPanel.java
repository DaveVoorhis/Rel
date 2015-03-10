package org.reldb.relui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

public abstract class TabPanel extends CTabItem {

	private Composite area;
	private TopPanel topPanel;
	
	public TabPanel(CTabFolder parent, int style) {
		super(parent, style);
		
		area = new Composite(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		area.setLayout(gridLayout);
		
		topPanel = new TopPanel(area, SWT.NONE) {
			public void notifyModeChange(String modeName) {
				TabPanel.this.notifyModeChange(modeName);
			}
			public void buildLocationPanel(TopPanel parent) {
				TabPanel.this.buildLocationPanel(parent);
			}
		};
		GridData gd_topPanel = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		topPanel.setLayoutData(gd_topPanel);
		
		setControl(area);
	}

	public void buildLocationPanel(TopPanel parent) {}

	public Composite getContentParent() {
		return area;
	}

	public ToolBar getToolBar() {
		return topPanel.getToolBar();
	}
	
	public void addMode(Image iconImage, String toolTipText, String modeName) {
		topPanel.addMode(iconImage, toolTipText, modeName);
	}
		
	public void setMode(int modeNumber) {
		topPanel.setMode(modeNumber);
	}
	
	/** Override to define mode change handling for this TabPanel. */
	public abstract void notifyModeChange(String modeName);
	
	public void setContent(Control comp) {
		GridData centre = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		comp.setLayoutData(centre);
		getContentParent().layout();
	}
	
}
