package org.reldb.dbrowser.handlers.tools;

import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.feedback.SuggestionboxDialog;

public class SubmitFeedback {
	public void execute(Shell shell) {
		SuggestionboxDialog.launch(shell);
	}
}
