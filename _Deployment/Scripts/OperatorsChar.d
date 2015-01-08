// Assorted CHAR operators, largely based on Java String methods.

OPERATOR IS_DIGITS(s CHARACTER) RETURNS BOOLEAN Java FOREIGN
	String sbuf = s.stringValue();
	for (int i=0; i<sbuf.length(); i++)
		if (!Character.isDigit(sbuf.charAt(i)))
			return ValueBoolean.select(context.getGenerator(), false);
	return ValueBoolean.select(context.getGenerator(), true);
END OPERATOR;

OPERATOR LENGTH(s CHAR) RETURNS INTEGER Java FOREIGN
	return ValueInteger.select(context.getGenerator(), s.stringValue().length());
END OPERATOR;

OPERATOR SUBSTRING(s CHAR, index INTEGER) RETURNS CHAR Java FOREIGN
// Substring, 0 based
	return ValueCharacter.select(context.getGenerator(), s.stringValue().substring((int)index.longValue()));
END OPERATOR;

OPERATOR SUBSTRING(s CHAR, beginindex INTEGER, endindex INTEGER) RETURNS CHAR Java FOREIGN
// Substring, 0 based
	return ValueCharacter.select(context.getGenerator(), s.stringValue().substring((int)beginindex.longValue(), (int)endindex.longValue()));
END OPERATOR;

OPERATOR COMPARE_TO(s CHAR, anotherString CHAR) RETURNS INTEGER Java FOREIGN 
//          Compares two strings lexicographically.
	return ValueInteger.select(context.getGenerator(), s.stringValue().compareTo(anotherString.stringValue()));
END OPERATOR;

OPERATOR COMPARE_TO_IGNORE_CASE(s CHAR, str CHAR) RETURNS INTEGER Java FOREIGN
//          Compares two strings lexicographically, ignoring case differences.
	return ValueInteger.select(context.getGenerator(), s.stringValue().compareToIgnoreCase(str.stringValue()));
END OPERATOR;

OPERATOR ENDS_WITH(s CHAR, suffx CHAR) RETURNS BOOLEAN Java FOREIGN
	return ValueBoolean.select(context.getGenerator(), s.stringValue().endsWith(suffx.stringValue()));
END OPERATOR;

OPERATOR EQUALS_IGNORE_CASE(s CHAR, anotherString CHAR) RETURNS BOOLEAN Java FOREIGN
//          Compares this String to another String, ignoring case considerations.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().equalsIgnoreCase(anotherString.stringValue()));
END OPERATOR;

OPERATOR INDEX_OF(s CHAR, str CHAR) RETURNS INTEGER Java FOREIGN
//          Returns the index within this string of the first occurrence of the specified substring.
	return ValueInteger.select(context.getGenerator(), s.stringValue().indexOf(str.stringValue()));
END OPERATOR;

OPERATOR INDEX_OF(s CHAR, str CHAR, fromIndex INTEGER) RETURNS INTEGER Java FOREIGN
//          Returns the index within this string of the first occurrence of the 
//          specified substring, starting at the specified index.
	return ValueInteger.select(context.getGenerator(), s.stringValue().indexOf(str.stringValue(), (int)fromIndex.longValue()));
END OPERATOR;

OPERATOR LAST_INDEX_OF(s CHAR, str CHAR) RETURNS INTEGER Java FOREIGN
//          Returns the index within this string of the rightmost occurrence of the specified substring.
	return ValueInteger.select(context.getGenerator(), s.stringValue().lastIndexOf(str.stringValue()));
END OPERATOR;

OPERATOR LAST_INDEX_OF(s CHAR, str CHAR, fromIndex INTEGER) RETURNS INTEGER Java FOREIGN
//          Returns the index within this string of the last occurrence of 
//          the specified substring, searching backward starting at the specified index.
	return ValueInteger.select(context.getGenerator(), s.stringValue().lastIndexOf(str.stringValue(), (int)fromIndex.longValue()));
END OPERATOR;

OPERATOR MATCHES(s CHAR, regex CHAR) RETURNS BOOLEAN Java FOREIGN
//          Tells whether or not this string matches the given regular expression.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().matches(regex.stringValue()));
END OPERATOR;

OPERATOR REGION_MATCHES(s CHAR, ignoreCase BOOLEAN, toffset INTEGER, other CHAR, ooffset INTEGER, len INTEGER) 
RETURNS BOOLEAN Java FOREIGN
//          Tests if two string regions are equal.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().regionMatches(ignoreCase.booleanValue(),
						(int)toffset.longValue(),
						other.stringValue(),
						(int)ooffset.longValue(),
						(int)len.longValue()));
END OPERATOR;

OPERATOR REPLACE_ALL(s CHAR, regex CHAR, replacement CHAR) RETURNS CHAR Java FOREIGN
//          Replaces each substring of this string that matches the given regular expression with the given replacement.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().replaceAll(regex.stringValue(), replacement.stringValue()));
END OPERATOR;

OPERATOR REPLACE_FIRST(s CHAR, regex CHAR, replacement CHAR) RETURNS CHAR Java FOREIGN
//          Replaces the first substring of this string that matches the given regular expression with the given replacement.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().replaceFirst(regex.stringValue(), replacement.stringValue()));
END OPERATOR;

/*
OPERATOR SPLIT(s CHAR, regex CHAR) RETURNS (ARRAY OF CHAR) Java FOREIGN
//          Splits this string around matches of the given regular expression.
	Array a = new Array(new ArrayType(TypeChar.getType()));
	String[] ss = s.stringValue().split(regex.stringValue());
	for (int i=0; i<ss.length; i++)
		a.append(ValueCharacter.select(context.getGenerator(), ss[i]));
	return a;
END OPERATOR;

OPERATOR SPLIT(s CHAR, regex CHAR, limit INTEGER) RETURNS (ARRAY OF CHAR) Java FOREIGN
//          Splits this string around matches of the given regular expression, up to n times.
	Array a = new Array(new ArrayType(TypeChar.getType()));
	String[] ss = s.stringValue().split(regex.stringValue(), (int)limit.longValue());
	for (int i=0; i<ss.length; i++)
		a.append(ValueCharacter.select(context.getGenerator(), ss[i]));
	return a;
END OPERATOR;
*/

OPERATOR STARTS_WITH(s CHAR, prefx CHAR) RETURNS BOOLEAN Java FOREIGN
//          Tests if this string starts with the specified prefix.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().startsWith(prefx.stringValue()));
END OPERATOR;

OPERATOR STARTS_WITH(s CHAR, prefx CHAR, toffset INTEGER) RETURNS BOOLEAN Java FOREIGN
//          Tests if this string starts with the specified prefix beginning at a specified index.
	return ValueBoolean.select(context.getGenerator(), s.stringValue().startsWith(prefx.stringValue(), (int)toffset.longValue()));
END OPERATOR;

OPERATOR TO_LOWER_CASE(s CHAR) RETURNS CHAR Java FOREIGN
	return ValueCharacter.select(context.getGenerator(), s.stringValue().toLowerCase());
END OPERATOR;

OPERATOR TO_UPPER_CASE(s CHAR) RETURNS CHAR Java FOREIGN
//          Converts all of the characters in this String to upper case using the rules of the default locale.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().toUpperCase());
END OPERATOR;

OPERATOR TRIM(s CHAR) RETURNS CHAR Java FOREIGN
//          Trim leading and trailing blanks.
	return ValueCharacter.select(context.getGenerator(), s.stringValue().trim());
END OPERATOR;
