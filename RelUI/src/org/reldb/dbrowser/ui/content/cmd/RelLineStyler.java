package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

// Adapted from http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/JavaSourcecodeViewer.htm
public class RelLineStyler implements LineStyleListener {
	RelScanner scanner = new RelScanner();
	Color[] tokenColors;
	Vector<int[]> blockComments = new Vector<int[]>();

	public static final int EOF = -1;
	public static final int EOL = 10;
	public static final int WORD = 0;
	public static final int WHITESPACE = 1;
	public static final int KEYWORD = 2;
	public static final int COMMENT = 3;
	public static final int STRING = 5;
	public static final int OTHER = 6;
	public static final int NUMBER = 7;
	public static final int MAXIMUM_TOKEN = 8;

	public RelLineStyler() {
		initializeColors();
		scanner = new RelScanner();
	}

	Color getColor(int type) {
		if (type < 0 || type >= tokenColors.length) {
			return null;
		}
		return tokenColors[type];
	}

	boolean inBlockComment(int start, int end) {
		for (int i = 0; i < blockComments.size(); i++) {
			int[] offsets = (int[]) blockComments.elementAt(i);
			// start of comment in the line
			if ((offsets[0] >= start) && (offsets[0] <= end))
				return true;
			// end of comment in the line
			if ((offsets[1] >= start) && (offsets[1] <= end))
				return true;
			if ((offsets[0] <= start) && (offsets[1] >= end))
				return true;
		}
		return false;
	}

	void initializeColors() {
		tokenColors = new Color[MAXIMUM_TOKEN];
		tokenColors[WORD] = SWTResourceManager.getColor(SWT.COLOR_BLACK);
		tokenColors[WHITESPACE] = SWTResourceManager.getColor(SWT.COLOR_BLACK);
		tokenColors[KEYWORD] = SWTResourceManager.getColor(SWT.COLOR_DARK_RED);
		tokenColors[COMMENT] = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		tokenColors[STRING] = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
		tokenColors[OTHER] = SWTResourceManager.getColor(SWT.COLOR_BLACK);
		tokenColors[NUMBER] = SWTResourceManager.getColor(SWT.COLOR_BLACK);
	}

	/**
	 * Event.detail line start offset (input) Event.text line text (input)
	 * LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
	 * (output) LineStyleEvent.background line background color (output)
	 */
	public void lineGetStyle(LineStyleEvent event) {
		Vector<StyleRange> styles = new Vector<StyleRange>();
		int token;
		StyleRange lastStyle;
		// If the line is part of a block comment, create one style for the
		// entire line.
		if (inBlockComment(event.lineOffset, event.lineOffset + event.lineText.length())) {
			styles.addElement(new StyleRange(event.lineOffset, event.lineText.length(), getColor(COMMENT), null));
			event.styles = new StyleRange[styles.size()];
			styles.copyInto(event.styles);
			return;
		}
		Color defaultFgColor = ((Control) event.widget).getForeground();
		scanner.setRange(event.lineText);
		token = scanner.nextToken();
		while (token != EOF) {
			if (token == OTHER) {
				// do nothing for non-colored tokens
			} else if (token != WHITESPACE) {
				Color color = getColor(token);
				// Only create a style if the token color is different than the
				// widget's default foreground color and the token's style is
				// not
				// bold. Keywords are bolded.
				if ((!color.equals(defaultFgColor)) || (token == KEYWORD)) {
					StyleRange style = new StyleRange(scanner.getStartOffset() + event.lineOffset, scanner.getLength(),
							color, null);
					if (token == KEYWORD) {
						style.fontStyle = SWT.BOLD;
					}
					if (styles.isEmpty()) {
						styles.addElement(style);
					} else {
						// Merge similar styles. Doing so will improve
						// performance.
						lastStyle = (StyleRange) styles.lastElement();
						if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
							lastStyle.length += style.length;
						} else {
							styles.addElement(style);
						}
					}
				}
			} else if ((!styles.isEmpty()) && ((lastStyle = (StyleRange) styles.lastElement()).fontStyle == SWT.BOLD)) {
				int start = scanner.getStartOffset() + event.lineOffset;
				lastStyle = (StyleRange) styles.lastElement();
				// A font style of SWT.BOLD implies that the last style
				// represents a java keyword.
				if (lastStyle.start + lastStyle.length == start) {
					// Have the white space take on the style before it to
					// minimize the number of style ranges created and the
					// number of font style changes during rendering.
					lastStyle.length += scanner.getLength();
				}
			}
			token = scanner.nextToken();
		}
		event.styles = new StyleRange[styles.size()];
		styles.copyInto(event.styles);
	}

	public void parseBlockComments(String text) {
		blockComments = new Vector<int[]>();
		StringReader buffer = new StringReader(text);
		int ch;
		boolean blkComment = false;
		int cnt = 0;
		int[] offsets = new int[2];
		boolean done = false;

		try {
			while (!done) {
				switch (ch = buffer.read()) {
				case -1: {
					if (blkComment) {
						offsets[1] = cnt;
						blockComments.addElement(offsets);
					}
					done = true;
					break;
				}
				case '/': {
					ch = buffer.read();
					if ((ch == '*') && (!blkComment)) {
						offsets = new int[2];
						offsets[0] = cnt;
						blkComment = true;
						cnt++;
					} else {
						cnt++;
					}
					cnt++;
					break;
				}
				case '*': {
					if (blkComment) {
						ch = buffer.read();
						cnt++;
						if (ch == '/') {
							blkComment = false;
							offsets[1] = cnt;
							blockComments.addElement(offsets);
						}
					}
					cnt++;
					break;
				}
				default: {
					cnt++;
					break;
				}
				}
			}
		} catch (IOException e) {
			// ignore errors
		}
	}

	/**
	 * A simple fuzzy scanner for Rel
	 */
	public class RelScanner {

		protected Hashtable<String, Integer> fgKeys = null;
		protected StringBuffer fBuffer = new StringBuffer();
		protected String fDoc;
		protected int fPos;
		protected int fEnd;
		protected int fStartToken;
		protected boolean fEofSeen = false;

		private String[] fgKeywords = {   
		    "ADD",
		    "ALL",
		    "ALTER",
		    "AND",
		    "ANNOUNCE",
		    "ARRAY",
		    "AS",
		    "ASC",
		    "AVG",
		    "AVGD",
		    "BASE",
		    "BACKUP",
		    "BEGIN",
		    "BUT",
		    "BY",
		    "CALL",
		    "CASE",
		    "COMMIT",
		    "COMPOSE",
		    "CONSTRAINT",
		    "COUNT",
		    "COUNTD",
		    "DELETE",
		    "DESC",
		    "DIVIDEBY",
		    "DO",
		    "DROP",
		    "D_INSERT",
		    "D_UNION",
		    "ELSE",
		    "END",
		    "<EOT>",
		    "EXACTLYD",
		    "EXACTLY",
		    "EXECUTE",
		    "EXTEND",
		    "EXTERNAL",
		    "FALSE",
		    "FOREIGN",
		    "FOR",
		    "FROM",
		    "GROUP",
		    "I_DELETE",
		    "I_MINUS",
		    "IF",
		    "IN",
		    "INIT",
		    "INSERT",
		    "INTERSECT",
		    "IS",
		    "JOIN",
		    "KEY",
		    "LEAVE",
		    "LOAD",
		    "~[",
		    "]~",
		    "MATCHING",
		    "MAX",
		    "MIN",
		    "MINUS",
		    "NOT",
		    "OPERATOR",
		    "ORDER",
		    "ORDERED",
		    "ORDINAL",
		    "OR",
		    "OUTPUT",
		    "PER",
		    "POSSREP",
		    "PREFIX",
		    "PRIVATE",
		    "PUBLIC",
		    "REAL",
		    "RELATION",
		    "REL",
		    "RENAME",
		    "RETURN",
		    "RETURNS",
		    "ROLLBACK",
		    "SAME_HEADING_AS",
		    "SAME_TYPE_AS",
		    "SET",
		    "SEMIJOIN" ,
		    "SEMIMINUS",
		    "SUFFIX",
		    "SUMD",
		    "SUMMARIZE",
		    "SUM",
		    "SYNONYMS",
		    "DEE",
		    "TABLE_DEE",
		    "DUM",
		    "TABLE_DUM",
		    "TCLOSE",
		    "THEN",
		    "TIMES",
		    "TO",
		    "TRANSACTION",
		    "TRUE",
		    "TUPLE",
		    "TUP",
		    "TYPE",
		    "TYPE_OF",
		    "UNGROUP",
		    "UNION",
		    "UNWRAP",
		    "UPDATES",
		    "UPDATE",
		    "VAR",
		    "VERSION",
		    "VIRTUAL",
		    "VIEW",
		    "WHEN",
		    "WHERE",
		    "WHILE",
		    "WITH",
		    "WRAP",
		    "WRITE",
		    "WRITELN",
		    "XOR",
		    "XUNION"
		};

		public RelScanner() {
			initialize();
		}

		/**
		 * Returns the ending location of the current token in the document.
		 */
		public final int getLength() {
			return fPos - fStartToken;
		}

		/**
		 * Initialize the lookup table.
		 */
		void initialize() {
			fgKeys = new Hashtable<String, Integer>();
			Integer k = new Integer(KEYWORD);
			for (int i = 0; i < fgKeywords.length; i++)
				fgKeys.put(fgKeywords[i], k);
		}

		/**
		 * Returns the starting location of the current token in the document.
		 */
		public final int getStartOffset() {
			return fStartToken;
		}

		/**
		 * Returns the next lexical token in the document.
		 */
		public int nextToken() {
			int c;
			fStartToken = fPos;
			while (true) {
				switch (c = read()) {
				case EOF:
					return EOF;
				case '/': // comment
					c = read();
					if (c == '/') {
						while (true) {
							c = read();
							if ((c == EOF) || (c == EOL)) {
								unread(c);
								return COMMENT;
							}
						}
					} else {
						unread(c);
					}
					return OTHER;
				case '\'': // char const
					for (;;) {
						c = read();
						switch (c) {
						case '\'':
							return STRING;
						case EOF:
							unread(c);
							return STRING;
						case '\\':
							c = read();
							break;
						}
					}

				case '"': // string
					for (;;) {
						c = read();
						switch (c) {
						case '"':
							return STRING;
						case EOF:
							unread(c);
							return STRING;
						case '\\':
							c = read();
							break;
						}
					}

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					do {
						c = read();
					} while (Character.isDigit((char) c));
					unread(c);
					return NUMBER;
				default:
					if (Character.isWhitespace((char) c)) {
						do {
							c = read();
						} while (Character.isWhitespace((char) c));
						unread(c);
						return WHITESPACE;
					}
					if (Character.isJavaIdentifierStart((char) c)) {
						fBuffer.setLength(0);
						do {
							fBuffer.append((char) c);
							c = read();
						} while (Character.isJavaIdentifierPart((char) c));
						unread(c);
						Integer i = (Integer) fgKeys.get(fBuffer.toString());
						if (i != null)
							return i.intValue();
						return WORD;
					}
					return OTHER;
				}
			}
		}

		/**
		 * Returns next character.
		 */
		protected int read() {
			if (fPos <= fEnd) {
				return fDoc.charAt(fPos++);
			}
			return EOF;
		}

		public void setRange(String text) {
			fDoc = text;
			fPos = 0;
			fEnd = fDoc.length() - 1;
		}

		protected void unread(int c) {
			if (c != EOF)
				fPos--;
		}
	}
}
