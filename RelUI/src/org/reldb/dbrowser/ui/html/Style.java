package org.reldb.dbrowser.ui.html;

import java.util.ArrayList;

import org.eclipse.swt.graphics.FontData;
import org.reldb.dbrowser.ui.preferences.PreferencePageCmd;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class Style {
	
	private int sizeAdjustment;
	
	public Style(int sizeAdjustment) {
		this.sizeAdjustment = sizeAdjustment;
	}
	
	private static final String[] formattedStyle = {
		"body {margin-top: 0px; margin-left: 0px; margin-right: 0px; margin-bottom: 0px}\n",
		"table {border-style: none; border-width: 0px;}\n",
		"td, tr, th {border-style: solid; border-width: 1px;}\n",
		".ok {color: green;}\n",
	    ".bad {color: red;}\n",
	    ".note {color: blue;}\n",
	    ".user {color: gray;}\n",
	    ".warn {color: gold;}\n",
	    ".notice {color: black;}\n",
	};
	
	private String getBodyFontStyleString() {
		FontData[] data = Preferences.getPreferenceFont(PreferencePageCmd.CMD_FONT);
		FontData datum = data[0];
		// eliminate leading '.', if there is one
		String fontName = (datum.getName().startsWith(".") ? datum.getName().substring(1) : datum.getName());
		return "body, p, td, th {font-family: " + fontName + ", sans-serif; font-size: " + (datum.getHeight() + sizeAdjustment) + "pt;}\n";
	}

	private String getHTMLStyle() {
		String out = "";
		for (String styleLine: formattedStyle)
			out += styleLine;
		out += getBodyFontStyleString();
		return out;
	}

	public String getHTMLDocument(String content) {
		return	 
			"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
			"<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-gb\" xml:lang=\"en-gb\">\n" +
			"<script type=\"text/javascript\">" +
			"function selectAll() {" +
			"    document.execCommand(\"selectall\", null, false);" +
			"}" +
			"function getSelectionHtml() {" +
			"    var html = \"\";" +
			"    if (typeof window.getSelection != \"undefined\") {" +
			"        var sel = window.getSelection();" +
			"        if (sel.rangeCount) {" +
			"            var container = document.createElement(\"div\");" +
			"            for (var i = 0, len = sel.rangeCount; i < len; ++i) {" +
			"                container.appendChild(sel.getRangeAt(i).cloneContents());" +
			"            }" +
			"            html = container.innerHTML;" +
			"        }" +
			"    } else if (typeof document.selection != \"undefined\") {" +
			"        if (document.selection.type == \"Text\") {" +
			"            html = document.selection.createRange().htmlText;" +
			"        }" +
			"    }" +
			"    return html;" +
			"}" +
			"function obtainSel() {" +
			"    window.status = getSelectionHtml();" +
			"}" +
			"</script>" +
			"<head>\n" +
			"<style type=\"text/css\">\n" +
			"<!--\n" +
			getHTMLStyle() +
			"-->\n" +
			"</style>\n" +
			"</head>\n" +
			"<body id=\"body\">\n" +
			content +
			"</body>\n" +
			"</html>";
	}
	
	public String getEmptyHTMLDocument() {
		return getHTMLDocument("");
	}
	
	public String[] getFormattedStyle() {
		ArrayList<String> style = new ArrayList<String>();
		style.add(getBodyFontStyleString());
		for (String styleLine: formattedStyle)
			style.add(styleLine);
		return style.toArray(new String[0]);
	}

}
