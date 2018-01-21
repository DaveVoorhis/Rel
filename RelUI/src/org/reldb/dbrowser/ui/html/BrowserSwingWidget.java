package org.reldb.dbrowser.ui.html;

import javax.swing.JTextPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class BrowserSwingWidget extends Composite {

	private JTextPane jTextPane;
	
	public BrowserSwingWidget(Composite parent) {
		super(parent, SWT.EMBEDDED);
	}
	
	public void copy() {
		if (jTextPane != null)
			jTextPane.copy();
	}

	public void setJTextPane(JTextPane jTextPane) {
		this.jTextPane = jTextPane;
	}
	
}
