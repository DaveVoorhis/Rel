package org.reldb.relui.dbui.html;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserNative implements HtmlBrowser {

	private Browser browser;
	private Font font;
	
	private static String cleanForHtml(String s) {
		return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "");
	}
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		this.font = font;
		try {
			browser = new Browser(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
			browser.setJavascriptEnabled(true);
		} catch (Throwable t) {
			return false;
		}
		clear();
		return true;
	}

	private String buildDoc(String content) {
		FontData[] data = font.getFontData();
		FontData datum = data[0];
		String text =
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
					     "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-gb\" xml:lang=\"en-gb\">" +
						 "<html>" +
					     "<head>" +
					     "<style type=\"text/css\">" +
					     "<!--" +
							"table {border-style: none; border-width: 0px;}" +
							"td, tr, th {border-style: solid; border-width: 1px;}" +
							".ok {color: green;}" +
						    ".bad {color: red;}" +
						    ".note {color: blue;}" +
						    ".user {color: gray;}" +
							"body, p, td {font-family: arial, helvetica, sans-serif; font-size: " + (datum.getHeight() - 2) + "pt;}" +
						 "-->" +
						 "</style>" +
						 "</head>" +
						 "<body>" +
						 content +
						 "</body>" +
						 "</html>";
		return text;
	}
	
	private String lastAppend = "";
	
	@Override
	public void clear() {
		browser.setText(buildDoc(""));
		lastAppend = "";
	}
	
	@Override
	public void appendHtml(String s) {
		lastAppend += s;
		browser.setText("");
		browser.execute(String.format("document.write('%s');", cleanForHtml(buildDoc(lastAppend))));
	}

	@Override
	public void scrollToBottom() {
		browser.execute("window.scrollTo(0, document.body.scrollHeight)");
	}

	@Override
	public Control getWidget() {
		return browser;
	}

}
