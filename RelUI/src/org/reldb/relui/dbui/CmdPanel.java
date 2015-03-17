package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.browser.Browser;

public class CmdPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		new Browser(sashForm, SWT.BORDER);
		
		new CmdPanelInput(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] {3, 1});
	}

	public void clearOutput() {
		// TODO Auto-generated method stub
		
	}

	public void saveOutputAsHtml() {
		// TODO Auto-generated method stub
		
	}

	public void saveOutputAsText() {
		// TODO Auto-generated method stub
		
	}

	public void copyOutputToInput() {
		// TODO Auto-generated method stub
		
	}

	public void setEnhancedOutput(boolean selection) {
		// TODO Auto-generated method stub
		
	}

	public void setShowOk(boolean selection) {
		// TODO Auto-generated method stub
		
	}

	public void setAutoclear(boolean selection) {
		// TODO Auto-generated method stub
		
	}

	public void setHeadingVisible(boolean selection) {
		// TODO Auto-generated method stub
		
	}

	public void setHeadingTypesVisible(boolean selection) {
		// TODO Auto-generated method stub
		
	}
}
