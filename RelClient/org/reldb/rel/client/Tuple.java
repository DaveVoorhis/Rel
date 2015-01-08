package org.reldb.rel.client;

import java.util.Vector;

public class Tuple extends Value {

	private Vector<String> attributeNames = new Vector<String>();
	private Vector<Value> attributeValues = new Vector<Value>();
	
	Tuple() {}
	
	void addAttributeName(String name) {
		attributeNames.add(name);
	}
	
	void addValue(Value value) {
		attributeValues.add(value);
	}

	/** Get quantity of attributes in this Tuple. */
	public int getAttributeCount() {
		return attributeValues.size();
	}
	
	/** Get ith attribute name. */
	public String getAttributeName(int i) {
		return attributeNames.get(i);
	}
	
	/** Get ith attribute value. */
	public Value getAttributeValue(int i) {
		return attributeValues.get(i);
	}
	
	/** Get index of a given attribute name. -1 if not found. */
	public int getIndexOf(String name) {
		return attributeNames.indexOf(name);
	}
	
	/** Get attribute Value for given attribute Name.  Return null if not found. */
	public Value getAttributeValue(String name) {
		int index = getIndexOf(name);
		if (index < 0)
			return null;
		return getAttributeValue(index);
	}
	
	/** Shortcut for getAttributeValue */
	public Value get(int i) {
		return getAttributeValue(i);
	}
	
	/** Shortcut for getAttributeValue */
	public Value get(String name) {
		return getAttributeValue(name);
	}
	
	/** True if this is the end-of-list Tuple in a set of TupleS. */
	public boolean isNull() {
		return false;
	}

	public int toInt() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to int.");
	}

	public long toLong() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to long.");
	}

	public double toDouble() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to double.");
	}

	public float toFloat() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Tuple can't be cast to float.");
	}

	public boolean toBoolean() throws InvalidValueException {
		throw new InvalidValueException("Tuple can't be cast to boolean.");
	}
	
	public String toString() {
		String tuples = "";
		for (int i=0; i<getAttributeCount(); i++) {
			if (tuples.length() > 0)
				tuples += ", ";
			tuples += getAttributeName(i) + " " + getAttributeValue(i).toString();
		}
		return "TUPLE {" + tuples + "}";
	}
	
}
