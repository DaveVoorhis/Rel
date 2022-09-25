package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

class TypeInfo {	
	private DbConnection connection;

	TypeInfo(DbConnection connection) {
		this.connection = connection;
	}
	
	String getKindFor(String typeInfo) {
		return connection.evaluate("THE_Kind(" + typeInfo + ")").toString();	
	}
	
	// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
	Tuples getAttributesFor(String typeInfo) {
		return connection.getTuples(
				"EXTEND THE_Attributes(" + typeInfo + "): " +
				"{AttrTypeName := " +
				"	IF IS_Scalar(AttrType) THEN " +
				"		THE_TypeName(TREAT_AS_Scalar(AttrType)) " + 
				"	ELSE " +
				"		THE_Kind(TREAT_AS_NonScalar(AttrType)) " + 
				"	END IF} " +
				"{AttrName, AttrTypeName, AttrType}");
	}
	    	
	String getHeadingDefinition(String typeInfo) {
		String kind = getKindFor(typeInfo);
		Tuples tuples = getAttributesFor(typeInfo);
		String body = "";
		if (tuples != null)				
			for (Tuple tuple: tuples) {
				String attrName = tuple.get(Attr.NAME_COLUMN).toString();
				String type = tuple.get(Attr.TYPE_COLUMN).toString();
				if (Attr.isNonScalar(type))
					type = getHeadingDefinition(tuple.get(Attr.HEADING_COLUMN).toString());
				body += ((body.length() > 0) ? ", " : "") + attrName + " " + type; 
			}
		return kind + " {" + body + "}";
	}

}
