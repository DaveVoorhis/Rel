package org.reldb.rel.dbrowser.style;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.reldb.rel.dbrowser.utilities.Preferences;

public class DBrowserStyle {
	
	private static final String[] formattedStyle = {
		"table {border-style: none; border-width: 0px;}",
		"td, tr, th {border-style: solid; border-width: 1px;}",
		".ok {color: green;}",
	    ".bad {color: red;}",
	    ".note {color: blue;}",
	    ".user {color: gray;}"
	};

	private static String getBodyFontStyleString(Font font) {
		return "body, td {font-family: " + font.getFamily() + "; font-size: " + font.getSize() + "pt;}";		
	}
	
	private static String getBodyFontStyleString() {
		return getBodyFontStyleString(Preferences.getInstance().getInputOutputFont());
	}

	public static void setBodyFontStyle(JTextPane pane, Font font) {
		HTMLEditorKit kit = (HTMLEditorKit)pane.getEditorKit();
		StyleSheet css = kit.getStyleSheet();
		css.addRule(getBodyFontStyleString(font));		
	}
	
	public static void setBodyFontStyle(JTextPane pane) {
		HTMLEditorKit kit = (HTMLEditorKit)pane.getEditorKit();
		StyleSheet css = kit.getStyleSheet();
		css.addRule(getBodyFontStyleString());
	}

	public static String[] getFormattedStyle() {
		ArrayList<String> style = new ArrayList<String>();
		style.add(getBodyFontStyleString());
		for (String styleLine: formattedStyle)
			style.add(styleLine);
		return style.toArray(new String[0]);
	}

	public static void setEnhancedOutputStyle(JTextPane pane) {
		pane.setContentType("text/html");
		pane.setEditable(false);
		HTMLEditorKit editorKit = new HTMLEditorKit();
		HTMLDocument defaultDocument = (HTMLDocument)editorKit.createDefaultDocument();
		pane.setEditorKit(editorKit);
		pane.setDocument(defaultDocument);
		StyleSheet css = editorKit.getStyleSheet();
		for (String entry: getFormattedStyle())
			css.addRule(entry);
	}

}
