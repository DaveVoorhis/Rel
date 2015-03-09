package org.reldb.relui.tools;

import java.util.HashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relui.tools.TabPanel;

public class ModeTab extends TabPanel {

	private Control displayed = null;
	private HashMap<String, ModeTabContent> modes = new HashMap<String, ModeTabContent>();
	private static int modeNumber = 0;
	
	public void addMode(String iconFilename, String toolTipText, ModeTabContent content) {
		String modeName = String.valueOf(modeNumber++);
		addMode(iconFilename, toolTipText, modeName);
		modes.put(modeName, content);
	}
	
	public ModeTab(CTabFolder parent, int style) {
		super(parent, style);
	}

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
