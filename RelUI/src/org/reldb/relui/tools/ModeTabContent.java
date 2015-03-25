package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

public interface ModeTabContent {
	public Control getContent(Composite contentParent);
	public void getToolBarItems(ToolBar toolBar);
	public void dispose();
}
