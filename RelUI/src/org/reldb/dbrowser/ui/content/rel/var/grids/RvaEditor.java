package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class RvaEditor extends RelvarEditor {
	
//	private String headingDefinition;
	
	// Relvar attribute designer
	public RvaEditor(Composite parent, DbConnection connection) {
		super(parent, connection, null);
		askDeleteConfirm = false;
	}

//	protected String getAttributeSource() {
//		return headingDefinition;
//	}

	public String getRVAValue() {
//		headingDefinition = dataProvider.getTypeInfoLiteral();
//		return headingDefinition;
		return null;
	}

	public void setRVAValue(String rvaValue) {
//		this.headingDefinition = headingDefinition;
//		init();
	}
	
}