package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelvarUI {
	
	protected boolean askDeleteConfirm = true;
	
	protected Vector<HashSet<String>> keys = new Vector<HashSet<String>>();
	
	protected Composite parent;
	protected DbConnection connection;
	protected String relvarName;

	public RelvarUI(Composite parent, DbConnection connection, String relvarName) {
		this.parent = parent;
		this.connection = connection;
		this.relvarName = relvarName;		
	}
	
	protected void obtainKeyDefinitions() {
		keys.clear();
		if (relvarName == null)
			return;
		Tuples keyDefinitions = (Tuples)connection.evaluate("((sys.Catalog WHERE Name = '" + relvarName + "') {Keys}) UNGROUP Keys");
		for (Tuple keyDefinition: keyDefinitions) {
			Tuples keyAttributes = (Tuples)(keyDefinition.get("Attributes"));
			HashSet<String> keyAttributeNames = new HashSet<String>();
			for (Tuple keyAttribute: keyAttributes) {
				String name = keyAttribute.get("Name").toString();
				keyAttributeNames.add(name);
			}
			keys.add(keyAttributeNames);
		}
	}
	
}