package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;

public abstract class TopPanel extends Composite {

	private ToolPanel tools;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unused")
	public TopPanel(Composite parent, int style) {
		super(parent, style);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		setLayout(layout);
		
		LocationPanel location = new LocationPanel(this, SWT.NONE);
		tools = new ToolPanel(this, SWT.NONE) {
			@Override
			public void notifyModeChange(String modeName) {
				TopPanel.this.notifyModeChange(modeName);
			}
		};
		
		pack();
	}

	public abstract void notifyModeChange(String modeName);
	
	public ToolBar getToolBar() {
		return tools.getToolBar();
	}

}
