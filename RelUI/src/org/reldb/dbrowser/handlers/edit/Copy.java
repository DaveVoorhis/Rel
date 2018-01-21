package org.reldb.dbrowser.handlers.edit;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.reldb.dbrowser.handlers.EditMenuItem;

public class Copy extends EditMenuItem {
	public Copy() {
		super("copy");
	}
	
	private boolean inBrowser() {
		return getControl() instanceof org.eclipse.swt.browser.Browser;		
	}
	
	public boolean canExecute(MHandledMenuItem item) {
		if (super.canExecute(item))
			return true;
		return inBrowser();
	}

	public void execute() {
		if (inBrowser()) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Copy Availability", 
					"The Copy drop-down menu item is not usable in the output panel.\n\nYou must right-click to select Copy from the pop-up menu.");	
			return;
		}
		super.execute();
	}
	
}
