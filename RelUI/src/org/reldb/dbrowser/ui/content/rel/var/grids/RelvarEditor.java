package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
import org.reldb.rel.client.Heading;

public class RelvarEditor extends Editor {

	public RelvarEditor(Composite parent, DbConnection connection, FilterSorter filterSorter) {
		super(parent, connection, filterSorter);
		syncFromDatabase();
		init();
	}

	private void syncFromDatabase() {
		refresh();
	}

	public void refresh() {
		obtainKeyDefinitions();
		tuples = obtainTuples();
		if (tuples != null) {
			Heading relvarHeading = tuples.getHeading();
			if (relvarHeading != null)
				heading = relvarHeading.toArray();
		}
		super.refresh();
	}

	protected String getAttributeSource() {
		return relvarName;
	}

}
