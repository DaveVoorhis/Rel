package org.reldb.relui.tools;

import java.util.HashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relui.tools.TabPanel;

public class ModeTab extends TabPanel {

	private Control displayed = null;
	private HashMap<String, ModeTabContent> modes = new HashMap<String, ModeTabContent>();
	private static int modeNumber = 0;
	
	public void addMode(Image iconImage, String toolTipText, ModeTabContent content) {
		String modeName = String.valueOf(modeNumber++);
		addMode(iconImage, toolTipText, modeName);
		modes.put(modeName, content);
	}
	
	public int countModes() {
		return modes.size();
	}
	
	public ModeTab(CTabFolder parent, int style) {
		super(parent, style);
	}
	
	public void buildLocationPanel(TopPanel parent) {}

	@Override
	public void notifyModeChange(String modeName) {
		if (displayed != null)
			displayed.dispose();
		
		ToolBar toolBar = getToolBar();
		
		for (ToolItem item: toolBar.getItems())
			item.dispose();

		ModeTabContent content = modes.get(modeName);
		displayed = content.getContent(getContentParent());
		setContent(displayed);

		content.getToolBarItems(toolBar);
		
		toolBar.pack();
	}

}
