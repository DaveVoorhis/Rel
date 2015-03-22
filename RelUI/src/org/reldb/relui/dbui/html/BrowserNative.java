package org.reldb.relui.dbui.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserNative implements HtmlBrowser {

	private Browser browser;
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		browser = new Browser(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendHtml(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scrollToBottom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Control getWidget() {
		return browser;
	}

}
