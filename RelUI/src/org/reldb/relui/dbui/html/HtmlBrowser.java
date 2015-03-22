package org.reldb.relui.dbui.html;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface HtmlBrowser {
	public boolean createWidget(Composite parent, Font font);
	public Control getWidget();
	public void clear();
	public void appendHtml(String s);
	public void scrollToBottom();
}
