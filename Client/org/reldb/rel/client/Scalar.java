package org.reldb.rel.client;

import org.reldb.rel.utilities.StringUtils;

public class Scalar extends Value {

	private String value = null;
	private boolean quoted = false;
	
	Scalar(String value, boolean quoted) {
		this.value = value;
		this.quoted = quoted;
	}

	Scalar() {}
	
	void addValue(Value value, boolean quoted) {
		this.value = value.toString();
		this.quoted = quoted;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString(int depth) {
		if (quoted && depth > 0)
			return "'" + StringUtils.quote(value) + "'";
		return value;
	}
	
	public String toString() {
		return value;
	}

	public int toInt() throws InvalidValueException, NumberFormatException {
		return Integer.parseInt(value);
	}

	public long toLong() throws InvalidValueException, NumberFormatException {
		return Long.parseLong(value);
	}

	public double toDouble() throws InvalidValueException, NumberFormatException {
		return Double.parseDouble(value);
	}

	public float toFloat() throws InvalidValueException, NumberFormatException {
		return Float.parseFloat(value);
	}

	public boolean toBoolean() throws InvalidValueException {
		return Boolean.parseBoolean(value);
	}

}
