package org.reldb.rel.client;

public class ScalarType extends Type {

	private String name;
	
	ScalarType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}
	
}
