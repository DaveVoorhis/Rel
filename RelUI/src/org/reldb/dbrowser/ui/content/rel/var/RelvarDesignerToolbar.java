package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarDesigner;

public class RelvarDesignerToolbar extends ManagedToolbar {
	
	public RelvarDesignerToolbar(Composite parent, RelvarDesigner relvarDesigner) {
		super(parent);
		
		addItem("Refresh", "arrow_refresh", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarDesigner.refresh();
			}
		});

		addItem("DELETE selected tuples", "table_row_delete", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarDesigner.askDeleteSelected();
			}
		});
	}

}
