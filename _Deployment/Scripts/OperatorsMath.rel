/** Math operators.  These are essentially a wrapper
    around the Java Math package. */

OPERATOR E() RETURNS RATIONAL Java FOREIGN 
// The RATIONAL value that is closer than any other to e, the base of
// the natural logarithms.
	return ValueRational.select(context.getGenerator(), Math.E);
END OPERATOR;

OPERATOR PI() RETURNS RATIONAL Java FOREIGN
// The RATIONAL value that is closer than any other to pi, the ratio of
// the circumference of a circle to its diameter.
	return ValueRational.select(context.getGenerator(), Math.PI);
END OPERATOR;

OPERATOR ABS(x RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the absolute value of a RATIONAL value.
	return ValueRational.select(context.getGenerator(), Math.abs(x.doubleValue()));
END OPERATOR;

OPERATOR ABS(x INTEGER) RETURNS INTEGER Java FOREIGN
// Returns the absolute value of a INTEGER value.
	return ValueInteger.select(context.getGenerator(), Math.abs(x.longValue()));
END OPERATOR;

OPERATOR ACOS(x RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the arc cosine of an angle, in the range of 0.0 through pi.
	return ValueRational.select(context.getGenerator(), Math.acos(x.doubleValue()));
END OPERATOR;

OPERATOR ASIN(x RATIONAL) RETURNS RATIONAL Java FOREIGN
//  Returns the arc sine of an angle, in the range of -pi/2 through pi/2.
	return ValueRational.select(context.getGenerator(), Math.asin(x.doubleValue()));
END OPERATOR;

OPERATOR ATAN(a RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the arc tangent of an angle, in the range of -pi/2 through
// pi/2.
	return ValueRational.select(context.getGenerator(), Math.atan(a.doubleValue()));
END OPERATOR;

OPERATOR ATAN2(y RATIONAL, x RATIONAL) RETURNS RATIONAL Java FOREIGN
// Converts rectangular coordinates (x, y) to polar (r, theta).
	return ValueRational.select(context.getGenerator(), Math.atan2(y.doubleValue(), x.doubleValue()));
END OPERATOR;

OPERATOR CEIL(a RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the smallest (closest to negative infinity) RATIONAL value
// that is not less than the argument and is equal to a mathematical
// integer.
	return ValueRational.select(context.getGenerator(), Math.ceil(a.doubleValue()));
END OPERATOR;

OPERATOR COS(a RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the trigonometric cosine of an angle.
	return ValueRational.select(context.getGenerator(), Math.cos(a.doubleValue()));
END OPERATOR;

OPERATOR EXP(a RATIONAL) RETURNS RATIONAL  Java FOREIGN      
// Returns Euler's number e raised to the power of a RATIONAL value.
	return ValueRational.select(context.getGenerator(), Math.exp(a.doubleValue()));
END OPERATOR;

OPERATOR FLOOR(a RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the largest (closest to positive infinity) RATIONAL value that
// is not greater than the argument and is equal to a mathematical
// integer.
	return ValueRational.select(context.getGenerator(), Math.floor(a.doubleValue()));
END OPERATOR;

OPERATOR REMAINDER(f1 RATIONAL, f2 RATIONAL) RETURNS RATIONAL  Java FOREIGN         
// Computes the remainder operation on two arguments as prescribed by the
// IEEE 754 standard.
	return ValueRational.select(context.getGenerator(), Math.IEEEremainder(f1.doubleValue(),
			f2.doubleValue()));
END OPERATOR;

OPERATOR LOG(a RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the natural logarithm (base e) of a RATIONAL value.
	return ValueRational.select(context.getGenerator(), Math.log(a.doubleValue()));
END OPERATOR;

OPERATOR MAXIMUM(a RATIONAL, b RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the greater of two RATIONAL values.
	return ValueRational.select(context.getGenerator(), Math.max(a.doubleValue(), b.doubleValue()));
END OPERATOR;

OPERATOR MAXIMUM(a INTEGER, b INTEGER) RETURNS INTEGER Java FOREIGN
// Returns the greater of two INTEGER values.
	return ValueInteger.select(context.getGenerator(), Math.max(a.longValue(), b.longValue()));
END OPERATOR;

OPERATOR MINIMUM(a RATIONAL, b RATIONAL) RETURNS RATIONAL Java FOREIGN
// Returns the smaller of two RATIONAL values.
	return ValueRational.select(context.getGenerator(), Math.min(a.doubleValue(), b.doubleValue()));
END OPERATOR;

OPERATOR MINIMUM(a INTEGER, b INTEGER) RETURNS INTEGER Java FOREIGN       
// Returns the smaller of two INTEGER values.
	return ValueInteger.select(context.getGenerator(), Math.min(a.longValue(), b.longValue()));
END OPERATOR;

OPERATOR POW(a RATIONAL, b RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the value of the first argument raised to the power of the
// second argument.
	return ValueRational.select(context.getGenerator(), Math.pow(a.doubleValue(), b.doubleValue()));
END OPERATOR;

OPERATOR RANDOM() RETURNS RATIONAL Java FOREIGN       
// Returns a RATIONAL value with a positive sign, greater than or equal
// to 0.0 and less than 1.0.
	return ValueRational.select(context.getGenerator(), Math.random());
END OPERATOR;

OPERATOR RINT(a RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the RATIONAL value that is closest in value to the argument
// and is equal to a mathematical integer.
	return ValueRational.select(context.getGenerator(), Math.rint(a.doubleValue()));
END OPERATOR;

OPERATOR ROUND(a RATIONAL) RETURNS INTEGER Java FOREIGN       
// Returns the closest INTEGER to the argument.
	return ValueInteger.select(context.getGenerator(), Math.round(a.doubleValue()));
END OPERATOR;

OPERATOR SIN(a RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the trigonometric sine of an angle.
	return ValueRational.select(context.getGenerator(), Math.sin(a.doubleValue()));
END OPERATOR;

OPERATOR SQRT(a RATIONAL) RETURNS RATIONAL Java FOREIGN          
// Returns the correctly rounded positive square root of a RATIONAL
// value.
	return ValueRational.select(context.getGenerator(), Math.sqrt(a.doubleValue()));
END OPERATOR;

OPERATOR TAN(a RATIONAL) RETURNS RATIONAL Java FOREIGN       
// Returns the trigonometric tangent of an angle.
	return ValueRational.select(context.getGenerator(), Math.tan(a.doubleValue()));
END OPERATOR;

OPERATOR TO_DEGREES(angrad RATIONAL) RETURNS RATIONAL Java FOREIGN          
// Converts an angle measured in radians to an approximately equivalent
// angle measured in degrees.
	return ValueRational.select(context.getGenerator(), Math.toDegrees(angrad.doubleValue()));
END OPERATOR;

OPERATOR TO_RADIANS(angdeg RATIONAL) RETURNS RATIONAL Java FOREIGN
// Converts an angle measured in degrees to an approximately equivalent
// angle measured in radians.
	return ValueRational.select(context.getGenerator(), Math.toRadians(angdeg.doubleValue()));
END OPERATOR;
