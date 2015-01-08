package org.reldb.rel.types;

/**
 * An attribute definition.
 */
public class Attribute implements Comparable<Attribute> {
	
	private String name;
	private Type type;

	public Attribute(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public final String getName() {
		return name;
	}
	
	public final Type getType() {
		return type;
	}
	
	public int compareTo(Attribute a) {
		return name.compareTo(a.name);
	}

	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		return name.equals(o);
	}
	
	public String toString() {
		return name + " " + type.getSignature();
	}
}