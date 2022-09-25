package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarDesigner;

public class VarRealDesignerToolbar extends ManagedToolbar {

	public VarRealDesignerToolbar(Composite parent, RelvarDesigner relvarDesigner) {
		super(parent);

		new CommandActivator(Commands.Do.Refresh, this, "arrow_refresh", SWT.PUSH, "Refresh", e -> relvarDesigner.refresh());
		new CommandActivator(null, this, "table_row_delete", SWT.PUSH, "DELETE selected tuples", e -> relvarDesigner.askDeleteSelected());
	}

}
