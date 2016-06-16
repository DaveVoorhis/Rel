package org.reldb.rel.v0.storage.tables;

import java.io.Serializable;

/** A tuple store name (name of Berkeley Database) and associated index names (also names of Berkeley DatabaseS). */
public class StorageNames implements Serializable {
	private final static long serialVersionUID = 0;
	
	private String[] names;
	
	public StorageNames(int storeCount) {
		names = new String[storeCount];
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
