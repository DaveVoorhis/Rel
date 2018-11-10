package org.reldb.dbrowser.handlers.file;

import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.AboutDialog;

public class About {
	public void execute(Shell shell) {
		new AboutDialog(shell).open();
	}
}
