package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.MainPanel;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentRev implements ModeTabContent {

	public DbTabContentRev(MainPanel main) {}

	@Override
	public Control getContent(Composite contentParent) {
		Label label = new Label(contentParent, SWT.None);
		label.setText("This is the Rev content.");
		return label;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
		ToolItem item7 = new ToolItem(toolBar, SWT.PUSH);
		item7.setImage(ResourceManager.getPluginImage("RelUI", "icons/loadIcon.png"));
		item7.setToolTipText("Load");

		ToolItem item8 = new ToolItem(toolBar, SWT.PUSH);
		item8.setImage(ResourceManager.getPluginImage("RelUI", "icons/pathIcon.png"));
		item8.setToolTipText("Insert file path");

		ToolItem item9 = new ToolItem(toolBar, SWT.PUSH);
		item9.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToInputIcon.png"));
		item9.setToolTipText("Copy output to input");

		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem item10 = new ToolItem(toolBar, SWT.CHECK);
		item10.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToOutputIcon.png"));
		item10.setToolTipText("Copy input to output");
		item10.setSelection(true);
		
		ToolItem item11 = new ToolItem(toolBar, SWT.CHECK);
		item11.setImage(ResourceManager.getPluginImage("RelUI", "icons/wrapIcon.png"));
		item11.setToolTipText("Wrap text");
		item11.setSelection(true);
	}

}
