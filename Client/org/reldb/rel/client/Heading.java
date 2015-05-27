package org.reldb.rel.client;

import java.util.Iterator;
import java.util.LinkedList;

public class Heading extends Type {
	
	private LinkedList<Attribute> attributes = new LinkedList<Attribute>();
	private String lastAttributeName;
	private String typeName;
	
	Heading(String typeName) {this.typeName = typeName;}
	
	void addAttributeName(String name) {
		lastAttributeName = name;
	}
	
	void addAttributeType(Type type) {
		attributes.add(new Attribute(lastAttributeName, type));
	}
	
	public Iterator<Attribute> getAttributes() {
		return attributes.iterator();
	}

	public Attribute[] toArray() {
		return (Attribute[])attributes.toArray(new Attribute[0]);
	}
	
	public int getCardinality() {
		return attributes.size();
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public String toString() {
		String attributeString = "";
		for (Attribute attribute: attributes) {
			if (attributeString.length() > 0)
				attributeString += ", ";
			attributeString += attribute;
		}
		return typeName + " {" + attributeString + "}";
	}
	
}
