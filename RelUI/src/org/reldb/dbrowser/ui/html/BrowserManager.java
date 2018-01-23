package org.reldb.dbrowser.ui.html;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.reldb.dbrowser.ui.preferences.PreferencePageCmd;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class BrowserManager implements HtmlBrowser {

	private HtmlBrowser browser = null;
	
	@Override
	public boolean createWidget(Composite parent) {
		if (browser != null)
			return true;
		return changeWidget(parent);
	}

	public boolean changeWidget(Composite parent) {
		String content = null;
		if (browser != null) {
			content = browser.getContent();
			browser.dispose();
		}
		if (!Preferences.getPreferenceBoolean(PreferencePageCmd.CMD_BROWSER_SWING)) {
			browser = new BrowserNative();
			if (browser.createWidget(parent)) {
				if (content != null)
					browser.setContent(content);
				return true;
			} else
				System.out.println("BrowserManager: Native browser is not available on this platform.");
		}
		browser = new BrowserSwing();
		browser.createWidget(parent);
		if (content != null)
			browser.setContent(content);
		return true;
	}

	@Override
	public void dispose() {
		browser.dispose();
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

	@Override
	public String getText() {
		return browser.getText();
	}

	@Override
	public String getSelectedText() {
		return browser.getSelectedText();
	}

	@Override
	public boolean isSelectedTextSupported() {
		return browser.isSelectedTextSupported();
	}

	@Override
	public Style getStyle() {
		return browser.getStyle();
	}

	@Override
	public void setContent(String content) {
		browser.setContent(content);
	}
	
	@Override
	public String getContent() {
		return browser.getContent();
	}
}
