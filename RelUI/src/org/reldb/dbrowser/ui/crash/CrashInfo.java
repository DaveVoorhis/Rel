package org.reldb.dbrowser.ui.crash;

import org.eclipse.swt.widgets.TreeItem;

public class CrashInfo {
	private String whatHappened;
	private String userEmail;
	private TreeItem report;

	public CrashInfo(String whatHappened, String userEmail, TreeItem report) {
		this.whatHappened = whatHappened;
		this.userEmail = userEmail;
		this.report = report;
	}

	// &amp;	&	ampersand 
	// &lt;		<	less than
	// &gt;		>	greater than
	// &apos;	'	apostrophe
	// &quot;	"	quotation mark
	private static String strXMLEncode(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	}
	
	private void emitChar(StringBuffer out, int count, char c) {
		for (int i=0; i<count; i++)
			out.append(c);
	}
	
	private String repeatChar(int count, char c) {
		StringBuffer s = new StringBuffer();
		emitChar(s, count, c);
		return s.toString();
	}

	private void emitOpenTag(StringBuffer out, String s) {
		out.append("<");
		out.append(s);
		out.append(">");		
	}
	
	private void emitCloseTag(StringBuffer out, String s) {
		out.append("</");
		out.append(s);
		out.append(">");
	}
	
	private boolean hasCheckOrCheckedChildren(TreeItem tree) {
		if (tree.getChecked())
			return true;
		for (TreeItem child: tree.getItems())
			if (hasCheckOrCheckedChildren(child))
				return true;
		return false;
	}
	
	private void emitTreeItems(StringBuffer out, int tabCount, TreeItem tree) {
		if (tree == null || !hasCheckOrCheckedChildren(tree))
			return;
		emitChar(out, tabCount, '\t');
		if (tree.getItemCount() == 0) {
			emitOpenTag(out, "data");
			out.append(strXMLEncode(tree.getText()).replace("\n", "\n" + repeatChar(tabCount, '\t')));
			emitCloseTag(out, "data");
		} else {
			String tagName = tree.getText().replace(' ', '_');
			emitOpenTag(out, tagName);
			out.append('\n');
			for (TreeItem child: tree.getItems())
				emitTreeItems(out, tabCount + 1, child);
			emitChar(out, tabCount, '\t');
			emitCloseTag(out, tagName);
		}
		out.append('\n');
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append("<RelErrorReport>\n");
		out.append("\t<WhatHappened>");
		if (whatHappened.trim().length() > 0)
			out.append("\n\t" + strXMLEncode(whatHappened).replace("\n", "\n\t") + "\n\t");
		out.append("</WhatHappened>\n");
		emitTreeItems(out, 1, report);
		out.append("\t<UserEmail>");
		if (userEmail.trim().length() > 0)
			out.append("\n\t" + strXMLEncode(userEmail) + "\n\t");
		out.append("</UserEmail>\n");
		out.append("</RelErrorReport>");
		return out.toString();
	}
}