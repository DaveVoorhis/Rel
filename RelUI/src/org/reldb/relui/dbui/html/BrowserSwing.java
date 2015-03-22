package org.reldb.relui.dbui.html;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserSwing implements HtmlBrowser {
	
	private Composite browserPanel;
	private JTextPane browser;
	private Font font;
	
	private static final String[] formattedStyle = {
		"table {border-style: none; border-width: 0px;}",
		"td, tr, th {border-style: solid; border-width: 1px;}",
		".ok {color: green;}",
	    ".bad {color: red;}",
	    ".note {color: blue;}",
	    ".user {color: gray;}"
	};

	private String getBodyFontStyleString() {
		FontData[] data = font.getFontData();
		FontData datum = data[0];
		return "body, td {font-family: arial, sans-serif; font-size: " + datum.getHeight() + "px;}";		
	}

	private String getHTMLStyle() {
		String out = "";
		for (String styleLine: formattedStyle)
			out += styleLine + '\n';
		out += getBodyFontStyleString();
		return out;
	}

	private String getEmptyHTMLDocument() {
		String out = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
				     "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-gb\" xml:lang=\"en-gb\">\n" +
				     "<head>\n" +
				     "<style type=\"text/css\">\n" +
				     "<!--\n" +
				     getHTMLStyle() + '\n' +
					 "-->\n" +
					 "</style>\n" +
					 "</head>\n" +
					 "<body>\n" +
					 "<div id=\"ctnt\"></div>" +
					 "</body>\n" +
					 "</html>";
		return out;
	}

	@Override
	public boolean createWidget(Composite parent, Font font) {
	    browserPanel = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(browserPanel);
	    
		browser = new JTextPane();
		Style.setEnhancedOutputStyle(browser, font);
		browser.setDoubleBuffered(true);
		DefaultCaret caret = (DefaultCaret)browser.getCaret();
	    caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	 		
		JScrollPane jScrollPaneOutput = new JScrollPane();
		jScrollPaneOutput.setAutoscrolls(true);
		jScrollPaneOutput.setViewportView(browser);
		
		frame.add(jScrollPaneOutput);
		
		return true;
	}

	@Override
	public void clear() {
		browser.setText(getEmptyHTMLDocument());
	}

	@Override
	public void appendHtml(String s) {
		HTMLDocument doc = (HTMLDocument)browser.getDocument();
		HTMLEditorKit kit = (HTMLEditorKit)browser.getEditorKit();
	    try {
	    	kit.insertHTML((HTMLDocument) doc, doc.getLength(), s, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void scrollToBottom() {}

	@Override
	public Control getWidget() {
		return browserPanel;
	}

}
