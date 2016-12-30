package org.reldb.rel.v0.storage.relvars.external;

// Based on http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes

public class CSVLineParse {	
    private static final String otherThanQuote = " [^\"] ";
    private static final String quotedString = String.format(" \" %s* \" ", otherThanQuote);
    private static final String regex = String.format(
    			"(?x) "+ 					  // enable comments, ignore white spaces
                ",                         "+ // match a comma
                "(?=                       "+ // start positive look ahead
                "  (?:                     "+ //   start non-capturing group 1
                "    %s*                   "+ //     match 'otherThanQuote' zero or more times
                "    %s                    "+ //     match 'quotedString'
                "  )*                      "+ //   end group 1 and repeat it zero or more times
                "  %s*                     "+ //   match 'otherThanQuote'
                "  $                       "+ // match the end of the string
                ")                         ", // stop positive look ahead
                otherThanQuote, quotedString, otherThanQuote);

	public static String[] parseRaw(String line) {
	    return line.split(regex, -1);
	}
	
	public static String[] parse(String line) {
		String[] parsed = parseRaw(line);
		for (int i = 0; i < parsed.length; i++) {
			String rawValue = parsed[i].trim();
			if (rawValue.startsWith("\"") && rawValue.endsWith("\""))
				parsed[i] = rawValue.substring(1, rawValue.length() - 1).replaceAll("\"\"", "\"");
		}
		return parsed;
	}

	public static String[] parseTrimmed(String line) {
		String[] parsed = parse(line);
		for (int i = 0; i < parsed.length; i++)
			parsed[i] = parsed[i].trim();
		return parsed;
	}
}
