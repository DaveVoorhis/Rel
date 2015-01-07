package ca.mb.armchair.rel3.debuginfo;

import ca.mb.armchair.rel3.languages.tutoriald.parser.Token;
import ca.mb.armchair.rel3.languages.tutoriald.BaseASTNode;

public class DebugInfo {

	private int line;
	private int column;
	private String nearestToken;
	private String location;
	
	public DebugInfo(String location) {
		this.line = -1;
		this.location = location;
	}
	
	public DebugInfo(int line, int column, String nearestToken, String location) {
		this.line = line;
		this.column = column;
		this.nearestToken = nearestToken;
		this.location = location;
	}
	
	public DebugInfo(Token token, String location) {
		this(token.beginLine, token.beginColumn, token.image, location);
	}
	
	public DebugInfo(BaseASTNode node, String location) {
		this(node.first_token.beginLine, node.first_token.beginColumn, node.tokenValue, location);
	}

	public String toString() {
		if (line == -1)
			return "\tIn " + location;
		else if (nearestToken == null)
			return "Line " + line + "\n" + location;
		else
			return "Line " + line + ", column " + column + " near '" + nearestToken + "'\n" + location;
	}
}
