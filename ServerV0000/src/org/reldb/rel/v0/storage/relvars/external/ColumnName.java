package org.reldb.rel.v0.storage.relvars.external;

public class ColumnName {
	/** Clean up a column title so it can be used as an identifier. */
	public static String cleanName(String name) {
		if (name == null)
			return "";
		String columnName = name.trim().replace(' ', '_').replace(',', '_').replace('-', '_').replace('@', '_');
		if (columnName.length() > 0) {
			char firstChar = columnName.charAt(0);
			if (Character.isDigit(firstChar))
				columnName = "_" + columnName;
		}
		return columnName;
	}
}
