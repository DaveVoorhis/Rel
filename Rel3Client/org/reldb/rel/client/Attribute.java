package org.reldb.rel.client;

public class Attribute {
	private String name;
	private Type type;

	public Attribute(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		return name + " " + type;
	}
}
