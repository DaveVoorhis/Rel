package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.widgets.Shell;

public class NewItemDialog extends FieldDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public NewItemDialog(Shell parentShell, String name) {
		super(parentShell, "Create", "Name:", name);
	}

	public String getName() {
		return getText();
	}
}
