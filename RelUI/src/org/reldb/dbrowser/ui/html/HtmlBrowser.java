package org.reldb.dbrowser.ui.html;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface HtmlBrowser {
	public boolean createWidget(Composite parent);
	public Control getWidget();
	public void clear();
	public void appendHtml(String s);
	public void scrollToBottom();
	public String getText();
	public String getSelectedText();
	public boolean isSelectedTextSupported();
	public Style getStyle();
	public void dispose();
	public void setContent(String content);
	public String getContent();
}
