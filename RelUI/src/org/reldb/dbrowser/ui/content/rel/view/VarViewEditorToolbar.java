package org.reldb.dbrowser.ui.content.rel.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.ManagedToolbar;

public class VarViewEditorToolbar extends ManagedToolbar {
	
	public VarViewEditorToolbar(Composite parent, QueryTable queryTable) {
		super(parent);
		
		addAdditionalItemsBefore(this);

		addItem("Refresh", "arrow_refresh", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				queryTable.refresh();
			}
		});
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore(VarViewEditorToolbar toolbar) {}

}
