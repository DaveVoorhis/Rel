package org.reldb.relui.dbui.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserNative implements HtmlBrowser {

	private Browser browser;
	private Style style;
	private StringBuffer text = new StringBuffer();
	
	private static String cleanForJavascriptInsertion(String s) {
		return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "");
	}
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.equals("mac os x"))
			style = new Style(font, -3);
		else
			style = new Style(font, 0);
		try {
			browser = new Browser(parent, SWT.BORDER);
			browser.setJavascriptEnabled(true);
		} catch (Throwable t) {
			return false;
		}
		clear();
		return true;
	}
	
	@Override
	public void clear() {
		browser.stop();
		browser.setText("");
		browser.setText(style.getEmptyHTMLDocument());
		browser.refresh();
		text = new StringBuffer();
	}
	
	private boolean appendTested = false;
	private boolean appendSupported = false;
	
	@Override
	public void appendHtml(String s) {
		if (!appendTested || appendSupported) {
			appendSupported = browser.execute("document.getElementById(\"body\").innerHTML += '" + cleanForJavascriptInsertion(s) + "'");
			appendTested = true;
			if (appendSupported)
				return;
		}
		browser.execute(String.format("document.write('%s');", cleanForJavascriptInsertion(style.getHTMLDocument(s))));
		text.append(s);
	}

	@Override
	public void scrollToBottom() {
		browser.execute("window.scrollTo(0, document.body.scrollHeight)");
	}

	@Override
	public Control getWidget() {
		return browser;
	}

	@Override
	public String getText() {
		return style.getHTMLDocument(text.toString());
	}

	@Override
	public String getSelectedText() {
		return null;
	}

	@Override
	public boolean isSelectedTextSupported() {
		return false;
	}

	@Override
	public Style getStyle() {
		return style;
	}

}
