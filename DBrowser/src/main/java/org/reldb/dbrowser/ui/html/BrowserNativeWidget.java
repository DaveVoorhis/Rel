package org.reldb.dbrowser.ui.html;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;

public class BrowserNativeWidget extends Browser {

	private StatusTextListener statusTextListener = evt -> {
		if (!evt.text.startsWith(Style.getSelectionIndicator()))
			return;
		BrowserManager.copyToClipboard(evt.text.substring(Style.getSelectionIndicator().length()));
	};
	
	public BrowserNativeWidget(Composite parent, int style) {
		super(parent, style);
		this.addStatusTextListener(statusTextListener);
	}

	public void dispose() {
		this.removeStatusTextListener(statusTextListener);
	}
	
	public void copy() {
		execute("obtainSel();");
	}

	public void selectAll() {
		execute("selectAll();");
	}
	
	public void checkSubclass() {}
}
