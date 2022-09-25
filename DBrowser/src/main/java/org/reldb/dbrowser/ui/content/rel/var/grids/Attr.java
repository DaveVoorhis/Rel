package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.reldb.rel.client.Tuple;

class Attr {
	public static final int NAME_COLUMN = 0;
	public static final int TYPE_COLUMN = 1;
	public static final int HEADING_COLUMN = 2;
	public static final int COLUMN_COUNT = 3;
	
	private String oldName;
	private String oldTypeName;
	private String oldHeading;

	private String newName;
	private String newTypeName;
	private String newHeading;
	
	Attr(Tuple tuple) {
		oldName = tuple.get(NAME_COLUMN).toString();
		oldTypeName = tuple.get(TYPE_COLUMN).toString();
		oldHeading = tuple.get(HEADING_COLUMN).toString();
		newName = null;
		newTypeName = null;
		newHeading = null;
	}
	
	Attr() {
		oldName = null;
		oldTypeName = null;
		oldHeading = null;
		newName = null;
		newTypeName = null;
		newHeading = null;
	}
	
	String getOriginalColumnValue(int column) {
		switch (column) {
		case NAME_COLUMN: return oldName;
		case TYPE_COLUMN: return oldTypeName;
		default: return oldHeading;
		}
	}
	
	String getNewColumnValue(int column) {
		switch (column) {
		case NAME_COLUMN: return newName;
		case TYPE_COLUMN: return newTypeName;
		default: return newHeading;		
		}
	}

	String getColumnValue(int column) {
		switch (column) {
		case NAME_COLUMN: return (newName != null) ? newName : oldName;
		case TYPE_COLUMN: return (newTypeName != null) ? newTypeName : oldTypeName;
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
			if (newTypeName.equals("RELATION"))
				newHeading = "NonScalar(\"RELATION\", RELATION {AttrName CHARACTER, AttrType TypeInfo} {})";
			else if (newTypeName.equals("TUPLE"))
				newHeading = "NonScalar(\"TUPLE\", RELATION {AttrName CHARACTER, AttrType TypeInfo} {})";
			else
				newHeading = null;
			break;
		default: 
			newHeading = (newValue != null) ? newValue.toString() : null;
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
		return isNameAndTypeFilled() && isNonScalar(typeName.toString());
	}

	static boolean isNonScalar(String type) {
		return type.equals("RELATION") || type.equals("TUPLE") || type.equals("ARRAY");	
	}
	
	boolean isNameAndTypeFilled() {
		Object name = getColumnValue(NAME_COLUMN);
		Object type = getColumnValue(TYPE_COLUMN);
		return (name != null && name.toString().trim().length() > 0 && 
				type != null && type.toString().trim().length() > 0);
	}
	
	boolean isFilled() {
		return isNameAndTypeFilled() && 
				((isEditableNonscalarDefinition() && getColumnValue(HEADING_COLUMN) != null) ||
				 !isEditableNonscalarDefinition());
	}
	
	// Convert this into a TypeInfo literal
	public String getTypeInfoLiteral() {
		if (isEditableNonscalarDefinition())
			return "TUPLE {AttrName '" + getColumnValue(NAME_COLUMN) + "', AttrType " + getColumnValue(HEADING_COLUMN) + "}";
		else
			return "TUPLE {AttrName '" + getColumnValue(NAME_COLUMN) + "', AttrType Scalar(\"" + getColumnValue(TYPE_COLUMN) + "\")}";
	}

	private boolean isChange(int column) {
		return getOriginalColumnValue(column) != null
				&& getNewColumnValue(column) != null
				&& !getOriginalColumnValue(column).equals(getNewColumnValue(column));		
	}
	
	boolean isNameChange() {
		return isChange(NAME_COLUMN);
	}
	
	boolean isTypeNameChange() {
		return isChange(TYPE_COLUMN);
	}
	
	boolean isHeadingChange() {
		return isEditableNonscalarDefinition() && isChange(HEADING_COLUMN);
	}
	
}