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
		item1.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		item1.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		item1.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		item2.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		item2.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		item3.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		item3.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		ToolItem item4 = new ToolItem(toolBar, SWT.PUSH);
		item4.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		item4.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		item4.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		ToolItem item5 = new ToolItem(toolBar, SWT.PUSH);
		item5.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		item5.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		item5.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		ToolItem item6 = new ToolItem(toolBar, SWT.PUSH);
		item6.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		item6.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		item6.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		
		ToolBar rightBar = new ToolBar(this, SWT.NONE);
		FormData fd_rightBar = new FormData();
		fd_rightBar.right = new FormAttachment(100);
		fd_rightBar.top = new FormAttachment(0);
		rightBar.setLayoutData(fd_rightBar);
		
		ToolItem sep = new ToolItem(rightBar, SWT.SEPARATOR);
		ToolItem rel = new ToolItem(rightBar, SWT.RADIO);
		rel.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRelIcon.png"));
		rel.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRelIcon.png"));
		rel.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRelIcon.png"));
		rel.setToolTipText("Rel");
		rel.setSelection(true);
		ToolItem rev = new ToolItem(rightBar, SWT.RADIO);
		rev.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRevIcon.png"));
		rev.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRevIcon.png"));
		rev.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRevIcon.png"));
		rev.setToolTipText("Rev");
		ToolItem cmd = new ToolItem(rightBar, SWT.RADIO);
		cmd.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/ModeCmdIcon.png"));
		cmd.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/ModeCmdIcon.png"));
		cmd.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeCmdIcon.png"));
		cmd.setToolTipText("Command line");
	}
}
