package ca.mb.armchair.rel3.storage.tables;

import java.io.Serializable;

/** A table (Database) and associated index (also DatabaseS) names. */
public class KeyTableNames implements Serializable {
	private final static long serialVersionUID = 0;
	
	private String[] names;
	
	public KeyTableNames(int tableCount) {
		names = new String[tableCount];
	}

	public String getName(int i) {
		return names[i];
	}
	
	public void setName(int i, String name) {
		names[i] = name;
	}
	
	public int size() {
		return names.length;
	}
}
