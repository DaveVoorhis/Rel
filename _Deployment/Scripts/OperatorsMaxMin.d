/** Maxima and minima. */

OPERATOR MAX_INTEGER() RETURNS INTEGER Java FOREIGN
// Largest positive integer
	return ValueInteger.select(context.getGenerator(), Long.MAX_VALUE);
END OPERATOR;

OPERATOR MIN_INTEGER() RETURNS INTEGER Java FOREIGN
// Largest negative integer
	return ValueInteger.select(context.getGenerator(), Long.MIN_VALUE);
END OPERATOR;

OPERATOR MAX_RATIONAL() RETURNS RATIONAL Java FOREIGN
// Largest positive rational
	return ValueRational.select(context.getGenerator(), Double.MAX_VALUE);
END OPERATOR;

OPERATOR MIN_RATIONAL() RETURNS RATIONAL Java FOREIGN
// Smallest positive rational
	return ValueRational.select(context.getGenerator(), Double.MIN_VALUE);
END OPERATOR;
