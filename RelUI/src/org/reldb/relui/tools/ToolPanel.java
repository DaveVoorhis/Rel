package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

import swing2swt.layout.BorderLayout;

public class ToolPanel extends Composite {
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		toolBar.setLayoutData(BorderLayout.WEST);
		ToolItem item1 = new ToolItem(toolBar, SWT.PUSH);
		item1.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item1.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item1.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		ToolItem item2 = new ToolItem(toolBar, SWT.PUSH);
		item2.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item2.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item2.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		ToolItem item3 = new ToolItem(toolBar, SWT.PUSH);
		item3.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item3.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item3.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		ToolItem item4 = new ToolItem(toolBar, SWT.PUSH);
		item4.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item4.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item4.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		ToolItem item5 = new ToolItem(toolBar, SWT.PUSH);
		item5.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item5.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item5.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		ToolItem item6 = new ToolItem(toolBar, SWT.PUSH);
		item6.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		item6.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		item6.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		
		ToolBar rightBar = new ToolBar(this, SWT.NONE);
		rightBar.setLayoutData(BorderLayout.EAST);
		ToolItem rel = new ToolItem(rightBar, SWT.PUSH);
		rel.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		rel.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		rel.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		rel.setToolTipText("Rel");
		ToolItem rev = new ToolItem(rightBar, SWT.PUSH);
		rev.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		rev.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		rev.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		rev.setToolTipText("Rev");
		ToolItem cmd = new ToolItem(rightBar, SWT.PUSH);
		cmd.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
		cmd.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
		cmd.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		cmd.setToolTipText("Command line");
	}
}
