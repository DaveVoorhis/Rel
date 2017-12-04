package org.reldb.dbrowser.handlers.tools;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class OpenPreferences {
	@Execute
	public void execute(Shell shell) {
		(new Preferences(shell)).show();
	}
}
