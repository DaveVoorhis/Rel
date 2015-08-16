package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuples;

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

	@Override
	protected Tuples obtainAttributes() {
		return connection.getTuples(
				"EXTEND THE_Attributes(" + getAttributeDefinition() + "): " +
				"{AttrTypeName := " +
				"	IF IS_Scalar(AttrType) THEN " +
				"		THE_TypeName(TREAT_AS_Scalar(AttrType)) " + 
				"	ELSE " +
				"		THE_Kind(TREAT_AS_NonScalar(AttrType)) " + 
				"	END IF} " +
				"{AttrName, AttrTypeName, AttrType}");
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