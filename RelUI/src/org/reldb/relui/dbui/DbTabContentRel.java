package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentRel implements ModeTabContent {

	@Override
	public Control getContent(Composite contentParent) {
		DemoContent content = new DemoContent(contentParent, SWT.None);
		return content;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {		
	}

}
