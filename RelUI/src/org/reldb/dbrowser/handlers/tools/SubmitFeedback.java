package org.reldb.dbrowser.handlers.tools;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.feedback.SuggestionboxDialog;

public class SubmitFeedback {
	@Execute
	public void execute(Shell shell) {
		SuggestionboxDialog.launch(shell);
	}
}
