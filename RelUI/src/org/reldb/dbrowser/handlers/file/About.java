package org.reldb.dbrowser.handlers.file;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.AboutDialog;

public class About {
	@Execute
	public void execute(Shell shell) {
		new AboutDialog(shell).open();
	}
}
