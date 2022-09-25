package org.reldb.rel.utilities;

import org.apache.commons.text.StringEscapeUtils;

public class StringUtils {

	/* Turn spaces to _ */
	public static String nospaces(String s) {
		if (s == null)
			return null;
		return s.replace(' ', '_');
	}
	
	/** Quote the given string by replacing unprintable characters by escape
	sequences.

	@param s the string to be quoted.
	@return a quoted string. */
	public static String quote(String s) {
		return StringEscapeUtils.escapeJava(s);
	}

	/** Unquote the given string and replace escape sequences by the
	    original characters.
	 
	@param s the string to be unquoted.
	@return an unquoted string. */
	public static String unquote(String s) {
		return StringEscapeUtils.unescapeJava(s);
	}

}
