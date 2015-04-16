package org.reldb.rel.v1.values;

/** Some string extension methods. 
 *
 * Based on PXLab at 
 * http://www.uni-mannheim.de/fakul/psycho/irtel/pxlab/index-download.html
 *
 *  @author Hans Irtel
 *  @version 0.1.8
 */

public class StringUtils {

	/** Quote the given string by replacing unprintable characters by escape
	sequences.
	 
	    From PXLab at 
	    http://www.uni-mannheim.de/fakul/psycho/irtel/pxlab/index-download.html
	   
	    @author Hans Irtel, with mods by Dave Voorhis
	    @version 0.1.9

	@param s the string to be quoted.
	@return a string which is a quoted representation of the input
	string. */
	public static String quote(String s) {
		char[] in = s.toCharArray();
		int n = in.length;
		StringBuffer out = new StringBuffer(n);
		for (int i = 0; i < n; i++) {
			switch (in[i]) {
			case '\n':
				out.append("\\n");
				break;
			case '\t':
				out.append("\\t");
				break;
			case '\b':
				out.append("\\b");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\f':
				out.append("\\f");
				break;
			case '\\':
				out.append("\\\\");
				break;
			case '\'':
				out.append("\\\'");
				break;
			case '\"':
				out.append("\\\"");
				break;
			default:
				out.append(new String(in, i, 1));
				break;
			}
		}
		return (out.toString());
	}

	/** Unquote the given string and replace escape sequences by the
	    original characters. 
	 
	    From PXLab at 
	    http://www.uni-mannheim.de/fakul/psycho/irtel/pxlab/index-download.html
	   
	    @author Hans Irtel, with mods by Dave Voorhis
	    @version 0.1.9
	 
	@param s the string to be unquoted.
	@return a string with quotes removed and escape sequences
	replaced by the respective character codes. */
	public static String unquote(String s) {
		char[] in = s.toCharArray();
		char[] out = new char[in.length];
		boolean inEscape = false;
		int k = 0;
		int n = in.length;
		for (int i = 0; i < n; i++) {
			if (inEscape) {
				switch (in[i]) {
				case 'n':
					out[k++] = '\n';
					break;
				case 't':
					out[k++] = '\t';
					break;
				case 'b':
					out[k++] = '\b';
					break;
				case 'r':
					out[k++] = '\r';
					break;
				case 'f':
					out[k++] = '\f';
					break;
				case '\\':
					out[k++] = '\\';
					break;
				case '\'':
					out[k++] = '\'';
					break;
				case '\"':
					out[k++] = '\"';
					break;
				default:
					out[k++] = '\\';
					out[k++] = in[i];
					break;
				}
				inEscape = false;
			} else {
				if (in[i] == '\\') {
					inEscape = true;
				} else {
					out[k++] = in[i];
				}
			}
		}
		return (new String(out, 0, k));
	}

}
