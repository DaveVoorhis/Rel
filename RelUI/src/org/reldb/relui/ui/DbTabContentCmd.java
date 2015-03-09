package org.reldb.relui.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentCmd implements ModeTabContent {

	@Override
	public Control getContent(Composite contentParent) {
		Label label = new Label(contentParent, SWT.None);
		label.setText("This is the Command Line content.");
		return label;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
		ToolItem item12 = new ToolItem(toolBar, SWT.CHECK);
		item12.setImage(ResourceManager.getPluginImage("RelUI", "icons/autoclearIcon.png"));
		item12.setToolTipText("Automatically clear output");
		item12.setSelection(true);
				
		ToolItem item13 = new ToolItem(toolBar, SWT.CHECK);
		item13.setImage(ResourceManager.getPluginImage("RelUI", "icons/enhancedIcon.png"));
		item13.setToolTipText("Display enhanced output");
		item13.setSelection(true);
		
		ToolItem item14 = new ToolItem(toolBar, SWT.CHECK);
		item14.setImage(ResourceManager.getPluginImage("RelUI", "icons/showOkIcon.png"));
		item14.setToolTipText("Write 'Ok.' after execution");
		item14.setSelection(true);
		
		ToolItem item15 = new ToolItem(toolBar, SWT.CHECK);
		item15.setImage(ResourceManager.getPluginImage("RelUI", "icons/headingIcon.png"));
		item15.setToolTipText("Show relation headings");
		item15.setSelection(true);
		
		ToolItem item16 = new ToolItem(toolBar, SWT.CHECK);
		item16.setImage(ResourceManager.getPluginImage("RelUI", "icons/typeSuppressIcon.png"));
		item16.setToolTipText("Suppress attribute types in relation headings");
		item16.setSelection(false);
	}

}
