package org.reldb.rel.client;

import java.util.LinkedList;

public class Selector extends Scalar {
	
	private String name;
	private LinkedList<Value> values = new LinkedList<Value>();
	
	Selector(String name) {
		this.name = name;
	}
	
	void addValue(Value r) {
		values.add(r);
	}

	public String toString() {
		String valueString = "";
		for (Value value: values) {
			if (valueString.length() > 0)
				valueString += ", ";
			valueString += value;
		}
		return name + "(" + valueString + ")";
	}
	
}
