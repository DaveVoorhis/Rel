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
	@SuppressWarnings("unused")
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

		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));

		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));

		ToolItem item4 = new ToolItem(toolBar, SWT.PUSH);
		item4.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));

		ToolItem item5 = new ToolItem(toolBar, SWT.PUSH);
		item5.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));

		ToolItem item6 = new ToolItem(toolBar, SWT.PUSH);
		item6.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));

		ToolItem item7 = new ToolItem(toolBar, SWT.PUSH);
		item7.setImage(ResourceManager.getPluginImage("RelUI", "icons/loadIcon.png"));

		ToolItem item8 = new ToolItem(toolBar, SWT.PUSH);
		item8.setImage(ResourceManager.getPluginImage("RelUI", "icons/pathIcon.png"));

		ToolItem item9 = new ToolItem(toolBar, SWT.PUSH);
		item9.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToInputIcon.png"));

		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem item10 = new ToolItem(toolBar, SWT.CHECK);
		item10.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToOutputIcon.png"));
		item10.setSelection(true);
		
		ToolItem item11 = new ToolItem(toolBar, SWT.CHECK);
		item11.setImage(ResourceManager.getPluginImage("RelUI", "icons/wrapIcon.png"));
		item11.setSelection(true);
				
		ToolBar rightBar = new ToolBar(this, SWT.NONE);
		FormData fd_rightBar = new FormData();
		fd_rightBar.right = new FormAttachment(100);
		fd_rightBar.top = new FormAttachment(0);
		rightBar.setLayoutData(fd_rightBar);
		
		ToolItem sep = new ToolItem(rightBar, SWT.SEPARATOR);
		
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
