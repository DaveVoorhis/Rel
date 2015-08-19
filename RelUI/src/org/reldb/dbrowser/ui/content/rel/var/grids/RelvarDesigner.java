package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class RelvarDesigner extends Designer {
	
	// Relvar designer
	public RelvarDesigner(Composite parent, DbConnection connection, String relvarName) {
		super(parent, connection, relvarName);
	    syncFromDatabase();
	    init();
	}
	
	private void syncFromDatabase() {
		obtainKeyDefinitions();		
		// Blank key definition allows user to add keys
		keys.add(new HashSet<String>());
	}

	public void refresh() {
		syncFromDatabase();
    	dataProvider.reload();
    	super.refresh();
	}
	
	protected String getAttributeSource() {
		return "Attributes FROM TUPLE FROM (sys.Catalog WHERE Name='" + relvarName + "')";
	}

}