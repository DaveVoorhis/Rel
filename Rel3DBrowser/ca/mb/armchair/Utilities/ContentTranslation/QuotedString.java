/*
 * QuotedString.java
 *
 * Created on September 7, 2002, 7:24 PM
 */

package ca.mb.armchair.Utilities.ContentTranslation;

/**
 * Quotify a string, or dequotify a string.
 *
 * @author  Dave Voorhis
 */
public class QuotedString {
    
    /** Creates a new instance of QuotedString */
    private QuotedString() {
    }
    
    /** Return a string with all the usual characters quoted.  Generally used
     * to convert raw strings into something that can be embedded
     * in Java source. */
    public static String getQuotedString(String s) {
        String out = "";
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\':
                    out += "\\\\"; break;
                case '\n':
                    out += "\\n"; break;
                case '\r':
                    out += "\\r"; break;
                case '\t':
                    out += "\\t"; break;
                case '"':
                    out += "\\\""; break;
                default:
                    out += c;
            }
        }
        return out;
    }
    
    /** Inverse of 'getQuotedString()' */
    public static String getUnQuotedString(String s) {
        String out = "";
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c=='\\') {
                i++;
                if (i>=s.length())
                    break;
                c = s.charAt(i);
                switch (c) {
                    case '\\':
                        out += '\\'; break;
                    case 'n':
                        out += '\n'; break;
                    case 'r':
                        out += '\r'; break;
                    case 't':
                        out += '\t'; break;
                    case '\"':
                        out += '\"'; break;
                    default:
                        out += '\\';
                }
            } else
                out += c;
        }
        return out;
    }
}
