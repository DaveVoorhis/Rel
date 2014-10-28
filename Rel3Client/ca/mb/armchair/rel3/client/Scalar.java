package ca.mb.armchair.rel3.client;

public class Scalar extends Value {

	private String value = null;
	
	Scalar(String value) {
		this.value = value;
	}

	Scalar() {}
	
	void addValue(Value v) {
		value = v.toString();
	}
	
	public String getValue() {
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
