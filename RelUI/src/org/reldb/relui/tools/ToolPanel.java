package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class ToolPanel extends Composite {
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		
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
		
		ToolBar rightBar = new ToolBar(this, SWT.NONE);
		FormData fd_rightBar = new FormData();
		fd_rightBar.right = new FormAttachment(100);
		fd_rightBar.top = new FormAttachment(0);
		rightBar.setLayoutData(fd_rightBar);
		
		new ToolItem(rightBar, SWT.SEPARATOR);
		
		ToolItem rel = new ToolItem(rightBar, SWT.RADIO);
		rel.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRelIcon.png"));
		rel.setToolTipText("Rel");
		rel.setSelection(true);
		
		ToolItem rev = new ToolItem(rightBar, SWT.RADIO);
		rev.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRevIcon.png"));
		rev.setToolTipText("Rev");
		
		ToolItem cmd = new ToolItem(rightBar, SWT.RADIO);
		cmd.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeCmdIcon.png"));
		cmd.setToolTipText("Command line");
	}
}
