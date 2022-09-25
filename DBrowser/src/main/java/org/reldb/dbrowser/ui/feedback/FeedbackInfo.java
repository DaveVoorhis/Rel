package org.reldb.dbrowser.ui.feedback;

import java.util.Vector;

import org.eclipse.swt.widgets.TreeItem;

public class FeedbackInfo {	
	private static class NameValue {
		String name;
		String value;
		public NameValue(String name, String value) {this.name = name; this.value = value;}
	}
	
	private String reportType;
	private Vector<NameValue> strings = new Vector<NameValue>();
	private Vector<TreeItem> trees = new Vector<TreeItem>();

	public FeedbackInfo(String reportType) {
		this.reportType = reportType;
	}

	public void addString(String name, String content) {
		strings.add(new NameValue(name, content));
	}

	public void addTree(TreeItem content) {
		trees.add(content);
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
		out.append("<" + reportType + ">\n");
		for (NameValue nameValue: strings) {
			out.append("\t<" + nameValue.name + ">");
			if (nameValue.value.trim().length() > 0)
				out.append("\n\t" + strXMLEncode(nameValue.value).replace("\n", "\n\t") + "\n\t");
			out.append("</" + nameValue.name + ">\n");			
		}
		for (TreeItem treeItem: trees)
			emitTreeItems(out, 1, treeItem);
		out.append("</" + reportType + ">");
		return out.toString();
	}
}