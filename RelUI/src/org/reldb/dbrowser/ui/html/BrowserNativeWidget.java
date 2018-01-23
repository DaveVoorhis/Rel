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
			System.out.println("BrowserNativeWidget: copy text = '" + text + "'");
			if (text == null || text.length() == 0) {
				System.out.println("BrowserNativeWidget: text is null.");
				return;
			}
			Clipboard clipboard = new Clipboard(parent.getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			if (textTransfer == null) {
				System.out.println("BrowserNativeWidget: TextTransfer is null. Copy of text to clipboard not supported.");
				return;
			}
			HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
			if (htmlTransfer == null) {
				System.out.println("BrowserNativeWidget: HTMLTransfer is null. Copy of HTML to clipboard not supported.");
				return;
			}
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
