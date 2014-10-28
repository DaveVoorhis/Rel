package ca.mb.armchair.rel3.client;

public class Error extends Value {

	private String errorMsg;
	
	Error(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	void addValue(Value r) throws InvalidValueException {
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
	
	public String toString() {
		return getErrorMsg();
	}
	
}
