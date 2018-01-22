package org.reldb.dbrowser.ui.html;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class BrowserNativeWidget extends Browser {

	public BrowserNativeWidget(Composite parent, int style) {
		super(parent, style);
	}

	public void copy() {
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Copy Availability", 
				"The Copy drop-down menu item is not usable in the output panel.\n\nYou must right-click to select Copy from the pop-up menu.");	
	}
}
