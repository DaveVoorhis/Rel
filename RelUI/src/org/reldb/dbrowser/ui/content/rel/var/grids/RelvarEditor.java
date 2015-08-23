package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class RelvarEditor extends Editor {
    
	public RelvarEditor(Composite parent, DbConnection connection, String relvarName) {
		super(parent, connection, relvarName);
	    syncFromDatabase();
	    init();
	}
	
	private void syncFromDatabase() {
		refresh();
	}

	public void refresh() {
		obtainKeyDefinitions();		
		tuples = obtainTuples();
    	heading = tuples.getHeading().toArray();
	}

	protected String getAttributeSource() {
		return relvarName;
	}

}
