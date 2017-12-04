package org.reldb.dbrowser.handlers.file;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Shell;

public class Quit {
	@Execute
	public void execute(IWorkbench workbench, Shell shell) {
		workbench.close();
	}
}
