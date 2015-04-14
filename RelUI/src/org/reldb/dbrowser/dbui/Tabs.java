package org.reldb.dbrowser.dbui;

public class Tabs {

	/** Convert a display column to a character index.
	 * A display column takes tabs into account, i.e., each tab is up to tabSize in length.
	 * A character index is the index into the string.  Each tab is one character.
	 * 
	 * @param tabSize
	 * @param s - a string that may contain tab characters
	 * @param displayColumn - 0-based display column
	 * @return character index - 0-based index into s
	 */
	public static int displayColumnToCharacterIndex(int tabSize, String s, int displayColumn) {
		int characterIndex = 0;
		int column = 0;
		while (column < displayColumn) {
			if (s.charAt(characterIndex) == '\t') {
				int tabOver = tabSize - (column % tabSize);
				if (tabOver == 0)
					tabOver = tabSize;
				column += tabOver;
			} else
				column++;
			characterIndex++;
			if (characterIndex >= s.length())
				break;
		}
		return characterIndex;
	}
	
	/** Convert a character index to a display column.
	 * A display column takes tabs into account, i.e., each tab is up to tabSize in length.
	 * A character index is the index into the string.  Each tab is one character.
	 * 
	 * @param tabSize
	 * @param s - a string that may contain tab characters
	 * @param characterIndex - 0-based index into s
	 * @return display column
	 */
	public static int characterIndexToDisplayColumn(int tabSize, String s, int characterIndex) {
		int displayColumn = 0;
		for (int i=0; i<characterIndex; i++)
			if (s.charAt(i) == '\t') {
				int tabOver = tabSize - (displayColumn % tabSize);
				if (tabOver == 0)
					tabOver = tabSize;
				displayColumn += tabOver;
			} else
				displayColumn++;
		return displayColumn;
	}
	
}
