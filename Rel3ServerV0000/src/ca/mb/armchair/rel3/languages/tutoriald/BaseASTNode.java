package ca.mb.armchair.rel3.languages.tutoriald;

import ca.mb.armchair.rel3.languages.tutoriald.parser.Token;

/** This is the base class for every AST node.  
 * 
 * tokenValue contains the actual value from which the token was constructed.
 * 
 * @author dave
 *
 */
public class BaseASTNode {
	public String tokenValue = null;
	public Token first_token;
	public Token last_token;
}
