package org.reldb.dbrowser.handlers.tools;

import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.feedback.BugReportDialog;

public class SubmitBugReport {
	public void execute(Shell shell) {
		BugReportDialog.launch(shell);
	}
}
