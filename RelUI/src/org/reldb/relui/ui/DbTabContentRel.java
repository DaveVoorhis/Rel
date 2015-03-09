package org.reldb.relui.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentRel implements ModeTabContent {

	@Override
	public Control getContent(Composite contentParent) {
		DemoContent content = new DemoContent(contentParent, SWT.None);
		return content;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {		
		ToolItem item1 = new ToolItem(toolBar, SWT.PUSH);
		item1.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		item1.setToolTipText("Clear");

		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		item2.setToolTipText("Backup");

		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		item3.setToolTipText("Save");

		ToolItem item4 = new ToolItem(toolBar, SWT.PUSH);
		item4.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		item4.setToolTipText("Save as text");

		ToolItem item5 = new ToolItem(toolBar, SWT.PUSH);
		item5.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		item5.setToolTipText("Save as HTML");

		ToolItem item6 = new ToolItem(toolBar, SWT.PUSH);
		item6.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		item6.setToolTipText("Save history");
	}

}
