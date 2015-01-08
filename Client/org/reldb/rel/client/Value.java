package org.reldb.rel.client; 

public abstract class Value {
	abstract void addValue(Value r) throws InvalidValueException;
	abstract public int toInt() throws InvalidValueException, NumberFormatException;
	abstract public long toLong() throws InvalidValueException, NumberFormatException;
	abstract public double toDouble() throws InvalidValueException, NumberFormatException;
	abstract public float toFloat() throws InvalidValueException, NumberFormatException;
	abstract public boolean toBoolean() throws InvalidValueException;
	abstract public String toString();
}
