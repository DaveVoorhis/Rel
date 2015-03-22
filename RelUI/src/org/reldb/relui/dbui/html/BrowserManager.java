package org.reldb.relui.dbui.html;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserManager implements HtmlBrowser {

	private HtmlBrowser browser;
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		browser = new BrowserNative();
		if (browser.createWidget(parent, font))
			return true;
		browser = new BrowserSwing();
		browser.createWidget(parent, font);
		return true;
	}

	@Override
	public void clear() {
		browser.clear();
	}

	@Override
	public void appendHtml(String s) {
		browser.appendHtml(s);
	}

	@Override
	public void scrollToBottom() {
		browser.scrollToBottom();
	}

	@Override
	public Control getWidget() {
		return browser.getWidget();
	}
}
