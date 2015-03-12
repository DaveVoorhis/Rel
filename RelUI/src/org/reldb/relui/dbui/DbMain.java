package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.reldb.relui.tools.MainPanel;

/** Root of RelUI. */
public class DbMain {
	private static MainPanel mainPanel;
	private static DirectoryDialog openDatabaseDialog;
	private static DirectoryDialog newDatabaseDialog;
	private static RemoteDatabaseDialog remoteDatabaseDialog;

	private static void initialise() {
		openDatabaseDialog = new DirectoryDialog(DbMain.getMainPanel().getShell());
		openDatabaseDialog.setText("Open Database");
		openDatabaseDialog.setMessage("Select a folder that contains a database.");
		openDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		newDatabaseDialog = new DirectoryDialog(DbMain.getMainPanel().getShell());
		newDatabaseDialog.setText("Create Database");
		newDatabaseDialog.setMessage("Select a folder to hold a new database.");
		newDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		remoteDatabaseDialog = new RemoteDatabaseDialog(DbMain.getMainPanel().getShell());
	}
	
	public static void setMainPanel(MainPanel mainPanel) {
		DbMain.mainPanel = mainPanel;
		initialise();
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

	public static void newDatabase() {
		Object result = newDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().newDatabase(result.toString());
	}

	public static void openLocalDatabase() {
		Object result = openDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openLocalDatabase(result.toString());
	}

	public static void openRemoteDatabase() {
		Object result = remoteDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openRemoteDatabase(result.toString());
	}

	public static void options() {
		// TODO Auto-generated method stub
	}
}
