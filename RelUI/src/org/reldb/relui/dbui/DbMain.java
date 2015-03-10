package org.reldb.relui.dbui;

import org.reldb.relui.tools.MainPanel;

/** Root of RelUI. */
public class DbMain {
	private static MainPanel mainPanel;
	
	public static void setMainPanel(MainPanel mainPanel) {
		DbMain.mainPanel = mainPanel;
	}
	
	public static MainPanel getMainPanel() {
		return mainPanel;
	}
	
	public static DbTab getCurrentDbTab() {
		return (DbTab)mainPanel.getTabFolder().getSelection();
	}

	public static void setSelection(int i) {
		mainPanel.getTabFolder().setSelection(0);
	}
}
