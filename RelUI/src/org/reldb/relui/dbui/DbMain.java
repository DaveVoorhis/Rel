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
		openDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		newDatabaseDialog = new DirectoryDialog(DbMain.getMainPanel().getShell());
		newDatabaseDialog.setText("Create Database");
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
		System.out.println("RESULT=" + newDatabaseDialog.open());
	}

	public static void openLocalDatabase() {
		System.out.println("RESULT=" + openDatabaseDialog.open());
	}

	public static void openRemoteDatabase() {
		System.out.println("RESULT=" + remoteDatabaseDialog.open());
	}

	public static void options() {
		// TODO Auto-generated method stub
		
	}
}
