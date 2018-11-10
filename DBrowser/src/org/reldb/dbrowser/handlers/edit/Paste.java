package org.reldb.dbrowser.handlers.edit;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.reldb.dbrowser.DecoratedMenuItem;
import org.reldb.dbrowser.handlers.EditMenuItem;

public class Paste extends EditMenuItem {
	public Paste() {
		super("paste");
	}
	
	private boolean isThereSomethingToPaste() {
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
			String textData = (String)clipboard.getContents(textTransfer);
			String htmlData = (String)clipboard.getContents(htmlTransfer);
			return (textData != null && textData.length() > 0) || (htmlData != null && htmlData.length() > 0);
		} finally {
			clipboard.dispose();	
		}
	}
	
	public boolean canExecute(DecoratedMenuItem item) {
		return super.canExecute(item) && isThereSomethingToPaste();
	}

}
