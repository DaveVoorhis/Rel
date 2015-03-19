package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.browser.Browser;

public class CmdPanel extends Composite {

	private Browser browser;
	private CmdPanelInput cmdPanelInput;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanel(DbTab dbTab, Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		browser = new Browser(sashForm, SWT.BORDER);
		
		cmdPanelInput = new CmdPanelInput(sashForm, SWT.NONE) {
			public void notifyCopyInputToOutput(String content) {
				browser.setText(browser.getText() + "<br>" + content);
			}
			public void notifyGo(String text) {
				browser.setText(browser.getText() + "<br>" + text);
				done();
			}
		};
		
		sashForm.setWeights(new int[] {3, 1});
	}

	public void clearOutput() {
		browser.setText("");
	}

	public void saveOutputAsHtml() {
		// TODO Auto-generated method stub
		
	}

	public void saveOutputAsText() {
		// TODO Auto-generated method stub
		
	}

	public void copyOutputToInput() {
		cmdPanelInput.setInputText("this is the text");
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
