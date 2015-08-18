package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.reldb.rel.client.Tuple;

class Attribute {
	private String oldName;
	private String oldTypeName;
	private String oldHeading;

	private String newName;
	private String newTypeName;
	private String newHeading;
	
	Attribute(Tuple tuple) {
		oldName = tuple.get(0).toString();
		oldTypeName = tuple.get(1).toString();
		oldHeading = tuple.get(2).toString();
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
		case 0: newName = newValue.toString(); break; 
		case 1: 
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
		return (getColumnValue(0) != null && getColumnValue(0).toString().length() > 0 && 
				getColumnValue(1) != null && getColumnValue(1).toString().length() > 0);
	}
	
	// Convert this into a TypeInfo literal
	public String getTypeInfoLiteral() {
		if (isEditableNonscalarDefinition())
			return "TUPLE {AttrName '" + getColumnValue(0) + "', AttrType " + getColumnValue(2) + "}";
		else
			return "TUPLE {AttrName '" + getColumnValue(0) + "', AttrType Scalar('" + getColumnValue(1) + "')}";
	}
}