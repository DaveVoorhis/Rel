package org.reldb.dbrowser.ui.html;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class BrowserNativeWidget extends Browser {

	public BrowserNativeWidget(Composite parent, int style) {
		super(parent, style);
		this.addStatusTextListener(evt -> {
			if (!evt.text.startsWith(Style.getSelectionIndicator()))
				return;
			String text = evt.text.substring(Style.getSelectionIndicator().length()).replace("<table", "<table border=\"1\"");
			Clipboard clipboard = new Clipboard(parent.getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
			Transfer[] transfers = new Transfer[] {textTransfer, htmlTransfer};
			Object[] data = new Object[] {text, text};
			clipboard.setContents(data, transfers);
			clipboard.dispose();			
		});
	}

	public void copy() {
		execute("obtainSel();");
	}

	public void selectAll() {
		execute("selectAll();");
	}
	
	public void checkSubclass() {
	}
}
