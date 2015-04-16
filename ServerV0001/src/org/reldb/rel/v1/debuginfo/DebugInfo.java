package org.reldb.rel.v1.debuginfo;

import org.reldb.rel.v1.languages.tutoriald.BaseASTNode;
import org.reldb.rel.v1.languages.tutoriald.parser.Token;

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
