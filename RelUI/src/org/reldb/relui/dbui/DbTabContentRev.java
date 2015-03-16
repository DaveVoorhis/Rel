package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentRev implements ModeTabContent {

	@Override
	public Control getContent(Composite contentParent) {
		Label label = new Label(contentParent, SWT.None);
		label.setText("This is the Rev content.");
		return label;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
	}

}
