package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.tools.ModeTabContent;

public class DbTabContentCmd implements ModeTabContent {
	
	@Override
	public Control getContent(Composite contentParent) {
		return new CmdPanel(contentParent, SWT.None);
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
		ToolItem clearBtn = new ToolItem(toolBar, SWT.PUSH);
		clearBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		clearBtn.setToolTipText("Clear");

		ToolItem saveAsHTMLBtn = new ToolItem(toolBar, SWT.PUSH);
		saveAsHTMLBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		saveAsHTMLBtn.setToolTipText("Save as HTML");

		ToolItem saveAsTextBtn = new ToolItem(toolBar, SWT.PUSH);
		saveAsTextBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		saveAsTextBtn.setToolTipText("Save as text");

		ToolItem copyToInputBtn = new ToolItem(toolBar, SWT.PUSH);
		copyToInputBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToInputIcon.png"));
		copyToInputBtn.setToolTipText("Copy output to input");

		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem enhancedOutputToggle = new ToolItem(toolBar, SWT.CHECK);
		enhancedOutputToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/enhancedIcon.png"));
		enhancedOutputToggle.setToolTipText("Display enhanced output");
		enhancedOutputToggle.setSelection(true);
		
		ToolItem showOkToggle = new ToolItem(toolBar, SWT.CHECK);
		showOkToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/showOkIcon.png"));
		showOkToggle.setToolTipText("Write 'Ok.' after execution");
		showOkToggle.setSelection(true);

		ToolItem autoclearToggle = new ToolItem(toolBar, SWT.CHECK);
		autoclearToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/autoclearIcon.png"));
		autoclearToggle.setToolTipText("Automatically clear output");
		autoclearToggle.setSelection(true);
		
		ToolItem headingToggle = new ToolItem(toolBar, SWT.CHECK);
		headingToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/headingIcon.png"));
		headingToggle.setToolTipText("Show relation headings");
		headingToggle.setSelection(true);
		
		ToolItem headingTypesToggle = new ToolItem(toolBar, SWT.CHECK);
		headingTypesToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/typeSuppressIcon.png"));
		headingTypesToggle.setToolTipText("Suppress attribute types in relation headings");
		headingTypesToggle.setSelection(false);	
	}

}
