package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

public abstract class TopPanel extends Composite {

	private ToolPanel tools;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TopPanel(Composite parent, int style) {
		super(parent, style);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		setLayout(layout);

		buildLocationPanel(this);
		
		tools = new ToolPanel(this, SWT.NONE) {
			@Override
			public void notifyModeChange(String modeName) {
				TopPanel.this.notifyModeChange(modeName);
			}
		};
		
		pack();
	}

	public abstract void notifyModeChange(String modeName);

	public void buildLocationPanel(TopPanel parent) {}
	
	public ToolBar getToolBar() {
		return tools.getToolBar();
	}
	
	public void addMode(Image iconImage, String toolTipText, String modeName) {
		tools.addMode(iconImage, toolTipText, modeName);
	}
		
	public void setMode(int modeNumber) {
		tools.setMode(modeNumber);
	}

}
