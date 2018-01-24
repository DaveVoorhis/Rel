package org.reldb.dbrowser.ui.html;

import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JTextPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

public class BrowserSwingWidget extends Composite {

	private JTextPane jTextPane;
	
	public BrowserSwingWidget(Composite parent) {
		super(parent, SWT.EMBEDDED);
	}
	
	private String getSelectionAsHTML() {
	    StringWriter buf = new StringWriter();
	    HTMLWriter writer = new HTMLWriter(buf,
	        (HTMLDocument)jTextPane.getDocument(), jTextPane.getSelectionStart(), jTextPane.getSelectionEnd());
	    try {
	        writer.write();
	    } catch (BadLocationException | IOException ex) {
	        ex.printStackTrace();
	    }
	    return buf.toString();
	}
	
	public void copy() {
		if (jTextPane != null)
			BrowserManager.copyToClipboard(getSelectionAsHTML());
	}

	public void selectAll() {
		if (jTextPane != null)
			jTextPane.selectAll();
	}
	
	public void setJTextPane(JTextPane jTextPane) {
		this.jTextPane = jTextPane;
	}
	
}
