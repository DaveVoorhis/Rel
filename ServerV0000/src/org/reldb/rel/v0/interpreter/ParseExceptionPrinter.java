package org.reldb.rel.v0.interpreter;

import org.reldb.rel.utilities.StringUtils;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.languages.tutoriald.parser.Token;

public class ParseExceptionPrinter {

	public static String getParseExceptionMessage(ParseException pe) {
	    StringBuffer expected = new StringBuffer();
	    int maxSize = 0;
	    int[][] expectedTokenSequences = pe.expectedTokenSequences;
	    String eol = System.getProperty("line.separator", "\n");
	    String[] tokenImage = pe.tokenImage;
	    Token currentToken = pe.currentToken;
	    for (int i = 0; i < expectedTokenSequences.length; i++) {
	      if (maxSize < expectedTokenSequences[i].length) {
	        maxSize = expectedTokenSequences[i].length;
	      }
	      for (int j = 0; j < expectedTokenSequences[i].length; j++) {
	        expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
	      }
	      if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
	        expected.append("...");
	      }
	      expected.append(eol).append("    ");
	    }
	    String retval = "Encountered ";
	    Token tok = currentToken.next;
	    for (int i = 0; i < maxSize; i++) {
	      if (i != 0) retval += " ";
	      if (tok.kind == 0) {
	        retval += tokenImage[0];
	        break;
	      }
	      retval += "\"";
	      retval += StringUtils.quote(tok.image);
	      retval += "\"";
	      tok = tok.next; 
	    }
	    retval += " at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
	    retval += "." + eol;
	    if (expectedTokenSequences.length == 1) {
	      retval += "Was expecting:" + eol + "    ";
	    } else {
	      retval += "Was expecting one of:" + eol + "    ";
	    }
	    retval += expected.toString();
	    return retval;		
	}
	
}
