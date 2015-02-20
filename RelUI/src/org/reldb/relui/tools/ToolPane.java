package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class ToolPane {

	private CBanner banner;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPane(Composite parent, int style) {		
		banner = new CBanner(parent, style);
		
		ToolBar rightBar = new ToolBar(banner, SWT.NONE);
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
		
		ToolBar toolBar = new ToolBar(banner, SWT.NONE);
		for (int i=0; i<8; i++) {
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			item.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16d.png"));
			item.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16h.png"));
			item.setImage(ResourceManager.getPluginImage("RelUI", "icons/makefg16.png"));
		}
		
		LocationPanel locationPanel = new LocationPanel(banner, SWT.NONE);
		
		banner.setLeft(locationPanel);
		banner.setRight(rightBar);
		banner.setBottom(toolBar);
		
		banner.setLocation(0, 0);
		
		banner.pack();
	}

	public void setLayoutData(Object ld) {
		banner.setLayoutData(ld);
	}
	
}
