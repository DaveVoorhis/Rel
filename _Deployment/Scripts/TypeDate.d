TYPE Date Java FOREIGN

// A simple Date type. 

	private java.util.Date d;

	/** Type constructor. */
	public Date(Generator generator) {
		super(generator);
		d = new java.util.Date(0);
	}

	/** Value constructor.  Construct date given a number of milliseconds
                         * since January 1, 1970, 00:00:00 GMT. */
	public Date(Generator generator, ValueInteger milliseconds) {
		super(generator);
		d = new java.util.Date(milliseconds.longValue());
	}

	/** Return this Date as a number of milliseconds since the epoch. */
	public ValueInteger milliseconds(Generator generator) {
		return ValueInteger.select(generator, d.getTime());
	}

	/** Return a Date in a readable format. */
	public ValueCharacter readable(Generator generator) {
		return ValueCharacter.select(generator, d.toString());
	}

	/** Compare this Value to another Value of the same Type. */
	public int compareTo(Value v) {
		return d.compareTo(((Date)v).d);
	}
    
	/** Return a parsable representation of this Value. */
	public String toParsableString(Type type) {
		return toString();
	}
    
	/** Return a string representation of this Value. */
	public String toString() {
		return "Date(" + d.getTime() + ")";
	}
    
	/** Get a default Value of this Type. */
	public Value getDefaultValue(Generator generator) {
		return new Date(generator, Now(generator));
	}

	/** Get current system time in milliseconds. */
	public static ValueInteger Now(Generator generator) {
		return ValueInteger.select(generator, System.currentTimeMillis());
	}

END TYPE;
