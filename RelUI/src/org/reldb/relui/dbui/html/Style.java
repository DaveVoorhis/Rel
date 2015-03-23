package org.reldb.relui.dbui.html;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class Style {
	
	private Font font;
	
	public Style(Font font) {
		this.font = font;
	}
	
	private Font font;
	private int sizeAdjustment;
	
	public Style(Font font, int sizeAdjustment) {
		this.font = font;
		this.sizeAdjustment = sizeAdjustment;
	}
	
	private static final String[] formattedStyle = {
		"body {margin-top: 0px; margin-left: 0px; margin-right: 0px; margin-bottom: 0px}",
		"table {border-style: none; border-width: 0px;}",
		"td, tr, th {border-style: solid; border-width: 1px;}",
		".ok {color: green;}",
	    ".bad {color: red;}",
	    ".note {color: blue;}",
	    ".user {color: gray;}"
	};

	public String getBodyFontStyleString() {
		FontData[] data = font.getFontData();
		FontData datum = data[0];
<<<<<<< HEAD
		return "body, p, td {font-family: arial, helvetica, sans-serif; font-size: " + (datum.getHeight() + sizeAdjustment) + "pt;}";
=======
		return "body, p, td {font-family: arial, helvetica, sans-serif; font-size: " + datum.getHeight() + "pt;}";
>>>>>>> branch 'master' of https://github.com/DaveVoorhis/Rel.git
	}

	private String getHTMLStyle() {
		String out = "";
		for (String styleLine: formattedStyle)
<<<<<<< HEAD
			out += styleLine;
=======
			out += styleLine + '\n';
>>>>>>> branch 'master' of https://github.com/DaveVoorhis/Rel.git
		out += getBodyFontStyleString();
		return out;
	}

<<<<<<< HEAD
	public String getHTMLDocument(String content) {
		String out = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
			     "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-gb\" xml:lang=\"en-gb\">" +
			     "<head>" +
			     "<style type=\"text/css\">" +
			     "<!--" +
			     getHTMLStyle() +
				 "-->" +
				 "</style>" +
				 "</head>" +
				 "<body>" +
				 content +
				 "</body>" +
				 "</html>";
		return out;
	}
	
	public String getEmptyHTMLDocument() {
		return getHTMLDocument("");
=======
	public String getEmptyHTMLDocument() {
		String out = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
				     "<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-gb\" xml:lang=\"en-gb\">\n" +
				     "<head>\n" +
				     "<style type=\"text/css\">\n" +
				     "<!--\n" +
				     getHTMLStyle() + '\n' +
					 "-->\n" +
					 "</style>\n" +
					 "</head>\n" +
					 "<body>\n" +
					 "Content" +
					 "</body>\n" +
					 "</html>";
		return out;
>>>>>>> branch 'master' of https://github.com/DaveVoorhis/Rel.git
	}
	
	public String[] getFormattedStyle() {
		ArrayList<String> style = new ArrayList<String>();
		style.add(getBodyFontStyleString());
		for (String styleLine: formattedStyle)
			style.add(styleLine);
		return style.toArray(new String[0]);
	}

}
