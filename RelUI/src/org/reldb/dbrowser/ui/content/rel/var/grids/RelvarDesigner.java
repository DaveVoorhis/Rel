package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuples;

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
    	table.refresh();		
	}
	
	// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
	protected Tuples obtainAttributes() {
		return connection.getTuples(
				"EXTEND THE_Attributes(Attributes FROM TUPLE FROM (sys.Catalog WHERE Name='" + relvarName + "')): " +
				"{AttrTypeName := " +
				"	IF IS_Scalar(AttrType) THEN " +
				"		THE_TypeName(TREAT_AS_Scalar(AttrType)) " + 
				"	ELSE " +
				"		THE_Kind(TREAT_AS_NonScalar(AttrType)) " + 
				"	END IF} " +
				"{AttrName, AttrTypeName, AttrType}");
	}

}