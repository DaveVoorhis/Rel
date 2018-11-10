package org.reldb.dbrowser.handlers.tools;

import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.updates.UpdatesCheckDialog;

public class CheckForUpdates {
	public void execute(Shell shell) {
		UpdatesCheckDialog.launch(shell);
	}
}
