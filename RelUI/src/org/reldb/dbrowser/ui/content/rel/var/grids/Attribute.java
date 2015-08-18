package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.reldb.rel.client.Tuple;

class Attribute {
	private static final int NAME_COLUMN = 0;
	private static final int TYPE_COLUMN = 1;
	private static final int HEADING_COLUMN = 2;
	
	private String oldName;
	private String oldTypeName;
	private String oldHeading;

	private String newName;
	private String newTypeName;
	private String newHeading;
	
	Attribute(Tuple tuple) {
		oldName = tuple.get(NAME_COLUMN).toString();
		oldTypeName = tuple.get(TYPE_COLUMN).toString();
		oldHeading = tuple.get(HEADING_COLUMN).toString();
		newName = null;
		newTypeName = null;
		newHeading = null;
	}
	
	Attribute() {
		oldName = null;
		oldTypeName = null;
		oldHeading = null;
		newName = null;
		newTypeName = null;
		newHeading = null;
	}
	
	Object getColumnValue(int column) {
		switch (column) {
		case 0: return (newName != null) ? newName : oldName;
		case 1: return (newTypeName != null) ? newTypeName : oldTypeName;
		default: return (!isEditableNonscalarDefinition()) ? null : ((newHeading != null) ? newHeading : oldHeading);
		}
	}
	
	void setColumnValue(int column, Object newValue) {
		if (newValue == null)
			return;
		switch (column) {
		case NAME_COLUMN: newName = newValue.toString(); break; 
		case TYPE_COLUMN: 
			if (newTypeName != null && newTypeName.equals(newValue.toString()))
				return;
			newTypeName = newValue.toString();
			newHeading = null;
			break;
		default: newHeading = (newValue != null) ? newValue.toString() : null;
		}
	}
	
	String getName() {
		if (newName == null) {
			if (oldName == null)
				return "";
			return oldName;
		}
		return newName;
	}
	
	boolean isEditableNonscalarDefinition() {
		Object typeName = getColumnValue(1);
		if (typeName == null)
			return false;
		return isFilled() && isNonScalar(typeName.toString());
	}

	static boolean isNonScalar(String type) {
		return type.equals("RELATION") || type.equals("TUPLE") || type.equals("ARRAY");	
	}

	boolean isFilled() {
		Object name = getColumnValue(NAME_COLUMN);
		Object type = getColumnValue(TYPE_COLUMN);
		return (name != null && name.toString().trim().length() > 0 && 
				type != null && type.toString().trim().length() > 0);
	}
	
	// Convert this into a TypeInfo literal
	public String getTypeInfoLiteral() {
		if (isEditableNonscalarDefinition())
			return "TUPLE {AttrName '" + getColumnValue(NAME_COLUMN) + "', AttrType " + getColumnValue(HEADING_COLUMN) + "}";
		else
			return "TUPLE {AttrName '" + getColumnValue(NAME_COLUMN) + "', AttrType Scalar('" + getColumnValue(TYPE_COLUMN) + "')}";
	}
}