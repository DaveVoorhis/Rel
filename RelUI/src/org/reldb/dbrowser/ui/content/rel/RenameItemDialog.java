package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.widgets.Shell;

public class RenameItemDialog extends FieldDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RenameItemDialog(Shell parentShell, String name) {
		super(parentShell, "Rename", "New name:", name);
	}

	public String getName() {
		return getText();
	}
}
