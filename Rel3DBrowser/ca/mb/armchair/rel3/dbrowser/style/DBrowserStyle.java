package ca.mb.armchair.rel3.dbrowser.style;

import java.util.ArrayList;

import javax.swing.JTextPane;

import ca.mb.armchair.rel3.dbrowser.utilities.Preferences;

public class DBrowserStyle {
	
	private static final String[] formattedStyle = {
		"body {font-family: Sans Serif;}",
		"table {border-style: none; border-width: 0px;}",
		"td, tr, th {border-style: solid; border-width: 1px;}"		
	};

	public static String[] getFormattedStyle() {
		ArrayList<String> style = new ArrayList<String>();
		style.add("body {font-size: " + Preferences.getInstance().getInputOutputFont().getSize() + "pt;}");
		for (String styleLine: formattedStyle)
			style.add(styleLine);
		return style.toArray(new String[0]);
	}

	public static void setEnhancedOutputStyle(JTextPane pane) {
		pane.setContentType("text/html");
		pane.setEditable(false);
		javax.swing.text.html.HTMLEditorKit editorKit = new javax.swing.text.html.HTMLEditorKit();
		javax.swing.text.html.HTMLDocument defaultDocument = (javax.swing.text.html.HTMLDocument) (editorKit.createDefaultDocument());
		pane.setEditorKit(editorKit);
		pane.setDocument(defaultDocument);
		javax.swing.text.html.StyleSheet css = editorKit.getStyleSheet();
		for (String entry: getFormattedStyle())
			css.addRule(entry);
	}

}
