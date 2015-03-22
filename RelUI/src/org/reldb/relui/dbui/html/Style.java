package org.reldb.relui.dbui.html;

import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class Style {
	
	private static final String[] formattedStyle = {
		"table {border-style: none; border-width: 0px;}",
		"td, tr, th {border-style: solid; border-width: 1px;}",
		".ok {color: green;}",
	    ".bad {color: red;}",
	    ".note {color: blue;}",
	    ".user {color: gray;}"
	};

	private static String getBodyFontStyleString(Font font) {
		FontData data[] = font.getFontData();
		FontData fontData = data[0];
		String style = "body, td {font-family: arial, sans-serif, " + fontData.getName() + "; font-size: " + (fontData.getHeight() - 2) + "px;}";
		return style;
	}

	public static void setBodyFontStyle(JTextPane pane, Font font) {
		HTMLEditorKit kit = (HTMLEditorKit)pane.getEditorKit();
		StyleSheet css = kit.getStyleSheet();
		css.addRule(getBodyFontStyleString(font));		
	}

	public static String[] getFormattedStyle(Font font) {
		ArrayList<String> style = new ArrayList<String>();
		style.add(getBodyFontStyleString(font));
		for (String styleLine: formattedStyle)
			style.add(styleLine);
		return style.toArray(new String[0]);
	}

	public static void setEnhancedOutputStyle(JTextPane pane, Font font) {
		pane.setContentType("text/html");
		pane.setEditable(false);
		HTMLEditorKit editorKit = new HTMLEditorKit();
		HTMLDocument defaultDocument = (HTMLDocument)editorKit.createDefaultDocument();
		pane.setEditorKit(editorKit);
		pane.setDocument(defaultDocument);
		StyleSheet css = editorKit.getStyleSheet();
		for (String entry: getFormattedStyle(font))
			css.addRule(entry);
	}

}
