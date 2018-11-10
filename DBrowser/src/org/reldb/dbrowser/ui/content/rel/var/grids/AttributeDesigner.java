package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbConnection;

public class AttributeDesigner extends Designer {
	
	private String headingDefinition;
	
	// Relvar attribute designer
	public AttributeDesigner(Composite parent, DbConnection connection) {
		super(parent, connection, null);
		askDeleteConfirm = false;
	}

	protected String getAttributeSource() {
		return headingDefinition;
	}

	public String getHeadingDefinition() {
		headingDefinition = dataProvider.getTypeInfoLiteral();
		return headingDefinition;
	}

	public void setHeadingDefinition(String headingDefinition) {
		this.headingDefinition = headingDefinition;
		init();
	}
	
}