package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class RvaEditor extends Editor {
	
	private String rvaValue;
	
	// Relvar attribute designer
	public RvaEditor(Composite parent, DbConnection connection) {
		super(parent, connection, null);
		askDeleteConfirm = false;
	}

	protected String getAttributeSource() {
		return rvaValue;
	}

	public String getRVAValue() {
		rvaValue = dataProvider.getLiteral();
		return rvaValue;
	}

	public void setRVAValue(String rvaValue) {
		this.rvaValue = rvaValue;
		tuples = obtainTuples();
    	heading = tuples.getHeading().toArray();
		init();
	}
	
}