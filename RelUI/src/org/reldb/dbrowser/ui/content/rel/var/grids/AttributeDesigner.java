package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class AttributeDesigner extends Designer {
	
	private String attributeDefinition;
	
	// Relvar attribute designer
	public AttributeDesigner(Composite parent, DbConnection connection) {
		super(parent, connection, null);
		askDeleteConfirm = false;
	}
	
	public void refresh() {
    	table.refresh();		
	}

	protected String getAttributeSource() {
		return getAttributeDefinition();
	}
	
	protected void obtainKeyDefinitions() {
		keys = null;
	}

	public String getAttributeDefinition() {
		return attributeDefinition;
	}

	public void setAttributeDefinition(String attributeDefinition) {
		this.attributeDefinition = attributeDefinition;
		init();
	}
}