package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.Application;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;

public class VarEditorToolbar extends ManagedToolbar {

	public VarEditorToolbar(Composite parent, RelvarEditor relvarEditor) {
		super(parent);

		addAdditionalItemsBefore(this);

		addItem(Application.getRefreshMenuItem(), "Refresh", "arrow_refresh", SWT.PUSH).addListener(SWT.Selection, e -> relvarEditor.refresh());
		addItem(null, "Go to INSERT row", "table_row_insert", SWT.PUSH).addListener(SWT.Selection,
				e -> relvarEditor.goToInsertRow());
		addItem(null, "DELETE selected tuples", "table_row_delete", SWT.PUSH).addListener(SWT.Selection,
				e -> relvarEditor.askDeleteSelected());
	}

	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore(VarEditorToolbar toolbar) {
	}
}
