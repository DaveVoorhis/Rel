package org.reldb.rel.client;

public class Error extends Value {

	private String errorMsg;
	
	Error(String errorMsg) {
		int EOTposition = errorMsg.indexOf("<EOT>");
		if (EOTposition >= 0)
			this.errorMsg = errorMsg.substring(0, EOTposition);
		else
			this.errorMsg = errorMsg;
	}
	
	void addValue(Value r, boolean b) throws InvalidValueException {
		throw new InvalidValueException("Invocation of addValue() on error.");
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public int toInt() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Error can't be cast to int.");
	}

	public long toLong() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Error can't be cast to long.");
	}

	public double toDouble() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Error can't be cast to double.");
	}

	public float toFloat() throws InvalidValueException, NumberFormatException {
		throw new InvalidValueException("Error can't be cast to float.");
	}

	public boolean toBoolean() throws InvalidValueException {
		throw new InvalidValueException("Error can't be cast to boolean.");
	}
	
	public String toString(int depth) {
		return getErrorMsg();
	}
	
	public String toString() {
		return toString(0);
	}
	
}
