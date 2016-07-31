package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;

public class RelvarEditorToolbar extends ManagedToolbar {
	
	public RelvarEditorToolbar(Composite parent, RelvarEditor relvarEditor) {
		super(parent);
		
		addAdditionalItemsBefore(this);

		addItem("Refresh", "arrow_refresh", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.refresh();
			}
		});

		addItem("Go to INSERT row", "table_row_insert", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.goToInsertRow();
			}
		});

		addItem("DELETE selected tuples", "table_row_delete", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.askDeleteSelected();
			}
		});
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore(RelvarEditorToolbar toolbar) {}

}
