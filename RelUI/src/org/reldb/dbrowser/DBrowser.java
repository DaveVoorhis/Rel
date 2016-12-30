package org.reldb.dbrowser;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.hooks.OpenDocumentEventProcessor;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.MainPanel;
import org.reldb.dbrowser.ui.ManageRecentlyUsedDialog;
import org.reldb.dbrowser.ui.RemoteDatabaseDialog;
import org.reldb.dbrowser.ui.RemoteDatabaseDialog.RemoteDatabaseDialogResponse;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

/** Root of RelUI. */
public class DBrowser {
	private final static String recentlyUsedDatabaseListPreference = "recentlyUsedDatabaseList";
	
	private static MainPanel mainPanel;
	private static DirectoryDialog openDatabaseDialog;
	private static DirectoryDialog newDatabaseDialog;
	private static RemoteDatabaseDialog remoteDatabaseDialog;

	private static OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor();
	
	private static boolean noLocalRel = true;

	private static String defaultDatabasePath = Paths.get(System.getProperty("user.home"), "DefaultRelDb").toString();

    public static Shell getShell() {
    	return mainPanel.getShell();
    }
    
    public static boolean isNoLocalRel() {
    	return noLocalRel;
    }

	public static void run(Composite parent) {
		parent.getDisplay().addListener(SWT.OpenDocument, openDocProcessor);		
		mainPanel = new MainPanel(parent, SWT.None);
		initialise();
	}
    
	public static void setStatus(String s) {
		mainPanel.setStatus(s);
	}
	
	private static void initialise() {
		openDatabaseDialog = new DirectoryDialog(getShell());
		openDatabaseDialog.setText("Open Database");
		openDatabaseDialog.setMessage("Select a folder that contains a database.");
		openDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		newDatabaseDialog = new DirectoryDialog(getShell());
		newDatabaseDialog.setText("Create Database");
		newDatabaseDialog.setMessage("Select a folder to hold a new database.  If a database already exists there, it will be opened.");
		newDatabaseDialog.setFilterPath(System.getProperty("user.home"));
		
		remoteDatabaseDialog = new RemoteDatabaseDialog(getShell());
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private int failureCount = 0;
			public void uncaughtException(Thread t, Throwable e) {
				if (failureCount > 1) {
					System.err.println("SYSTEM ERROR!  It's gotten even worse.  This is a last-ditch attempt to escape.");
					failureCount++;
					Thread.setDefaultUncaughtExceptionHandler(null);
					System.exit(1);
					return;
				} if (failureCount > 0) {
					System.err.println("SYSTEM ERROR!  Things have gone so horribly wrong that we can't recover or even pop up a message.  I hope someone sees this...\nShutting down now, if we can.");
					failureCount++;
					System.exit(1);
					return;
				}
				failureCount++;
				if (e instanceof OutOfMemoryError) {
					System.err.println("Out of memory!");
					e.printStackTrace();
					mainPanel.dispose();
					MessageDialog.openError(getShell(), "OUT OF MEMORY", "Out of memory!  Shutting down NOW!");
				} else {
					System.err.println("Unknown error: " + t);
					e.printStackTrace();
					mainPanel.dispose();
					MessageDialog.openError(getShell(), "Unexpected Error", e.toString());
				}
				System.exit(1);						
			}				
		});
		
    	try {
    		Class.forName("org.reldb.rel.Rel");
    		noLocalRel = false;
    	} catch (ClassNotFoundException cnfe) {
    		noLocalRel = true;
        }
    	
    	DbTab dbTab = new DbTab();
    	if (!Preferences.getPreferenceBoolean(PreferencePageGeneral.SKIP_DEFAULT_DB_LOAD))
    		dbTab.openDefaultDatabase(defaultDatabasePath);

 		openDocProcessor.addFilesToOpen(Activator.getApplicationArguments());
		String[] filesToOpen = openDocProcessor.retrieveFilesToOpen();
		for (String fname: filesToOpen)
			openFile(fname);
		
		DBrowser.setSelection(0);
	}

	public static CTabFolder getTabFolder() {
		return mainPanel.getTabFolder();
	}
	
	public static DbTab getCurrentDbTab() {
		return (DbTab)mainPanel.getTabFolder().getSelection();
	}

	public static void setSelection(int i) {
		mainPanel.getTabFolder().setSelection(i);
	}

	public static void newDatabase() {
		String result = newDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().newDatabase(result.toString());
	}

	public static void openLocalDatabase() {
		String result = openDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openLocalDatabase(result.toString());
	}

	public static void openRemoteDatabase() {
		RemoteDatabaseDialogResponse result = remoteDatabaseDialog.open();
		if (result != null)
			getCurrentDbTab().openRemoteDatabase(result.toString());
	}
	
	public static void createNewTabIfNeeded() {
		CTabItem[] tabs = mainPanel.getTabFolder().getItems();
		if (tabs.length == 0 || ((DbTab)tabs[tabs.length - 1]).isOpenOnADatabase())
			new DbTab();
	}

	public static DbTab selectEmptyTab() {
		CTabItem[] tabs = mainPanel.getTabFolder().getItems();
		DbTab lastTab = (DbTab)tabs[tabs.length - 1];
		DBrowser.setSelection(tabs.length - 1);
		return lastTab;
	}
	
	public static void openFile(String fname) {
		final String clickToOpenName = "ClickToOpen.rdb";
		if (fname.toLowerCase().endsWith(".rel")) {
			System.out.println("Request received to open " + fname);
			DbTab dbTab = selectEmptyTab();
			if (dbTab.openDefaultDatabase(defaultDatabasePath))
				dbTab.openFile(fname);
		} else {
			int fnamePos = fname.toLowerCase().indexOf(clickToOpenName.toLowerCase());
			if (fnamePos >= 0) {
				System.out.println("Request received to open " + fname);
				selectEmptyTab().openLocalDatabase(fname.substring(0, fnamePos));
			} else {
				String fnameWithRdb = fname + File.separator + clickToOpenName;
				if (new File(fnameWithRdb).exists()) {
					System.out.println("Request received to open database at " + fname);
					selectEmptyTab().openLocalDatabase(fname);
				}
			}
		}
	}

	public static void openDatabase(String dbURI) {
		getCurrentDbTab().openDatabaseAtURI(dbURI, false);
	}

	// Add this to the recently-used list if it's not there; move it up if it is
	public static void updateRecentlyUsedDatabaseList(String dbURL) {
		LinkedList<String> recentlyUsed = new LinkedList<String>();
		recentlyUsed.addAll(Arrays.asList(Preferences.getPreferenceStringArray(recentlyUsedDatabaseListPreference)));
		int indexOfDBURL = recentlyUsed.indexOf(dbURL);
		if (indexOfDBURL >= 0)
			recentlyUsed.remove(dbURL);
		recentlyUsed.addFirst(dbURL);
		if (recentlyUsed.size() > 15)	// arbitrary maximum 15 items in the recently used database list
			recentlyUsed.removeLast();
		Preferences.setPreference(recentlyUsedDatabaseListPreference, recentlyUsed.toArray(new String[0]));		
	}

	public static void clearRecentlyUsedDatabaseList() {
		if (MessageDialog.openConfirm(getShell(), "Clear list of recently-opened databases?", 
				"Are you sure you wish to clear the list of recently-opened databases?"))		
			Preferences.setPreference(recentlyUsedDatabaseListPreference, new String[0]);
	}

	public static void manageRecentlyUsedDatabaseList() {
		ManageRecentlyUsedDialog dialog = new ManageRecentlyUsedDialog(getShell());
		dialog.open();
	}

	public static String[] getRecentlyUsedDatabaseList() {
		return Preferences.getPreferenceStringArray(recentlyUsedDatabaseListPreference);
	}

	public static void setRecentlyUsedDatabaseList(String[] usedList) {
		Preferences.setPreference(recentlyUsedDatabaseListPreference, usedList);
	}
	
}
