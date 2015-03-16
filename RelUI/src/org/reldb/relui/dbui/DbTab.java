package org.reldb.relui.dbui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.ResourceManager;

import org.reldb.rel.client.string.ClientFromURL;
import org.reldb.rel.client.string.StringReceiverClient;

import org.reldb.relui.tools.ModeTab;
import org.reldb.relui.tools.ModeTabContent;
import org.reldb.relui.tools.TopPanel;

public class DbTab extends ModeTab {
	
	private LocationPanel locationPanel;
    
    private static class AttemptConnectionResult {
    	Throwable exception;
    	StringReceiverClient client;
    	public AttemptConnectionResult(Throwable exception) {
    		this.exception = exception;
    		this.client = null;
    	}
    	public AttemptConnectionResult(StringReceiverClient client) {
    		this.exception = null;
    		this.client = client;
    	}
    }
    
    private static String shortened(String s) {
    	if (s.length() < 80)
    		return s;
    	return s.replace(": ",":\n");
    }
    
    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
		if (countModes() == 0)
			new DbTab();
		setText(dbURL);
		setShowClose(true);
        DbMain.setStatus("Ok");    	
    }
    
    private void doConnectionResultFailed(String reason, String dbURL) {
    	String msg = "Unable to establish connection to " + dbURL + " - " + reason;
        DbMain.setStatus(msg);
        msg = shortened(msg);
        if (msg.contains("The environment cannot be locked for single writer access. ENV_LOCKED")) {
        	MessageDialog.openError(DbMain.getShell(), "Unable to open local database",
        			"A copy of Rel is already accessing the database you're trying to open at " + dbURL);
    	} else if (msg.contains("Connection refused")) {
    		MessageDialog.openError(DbMain.getShell(), "Unable to open remote database",
    				"A Rel server doesn't appear to be running or available at " + dbURL);
    	} else if (msg.contains("RS0406:")) {
    		MessageDialog.openError(DbMain.getShell(), "Unable to open local database",
    				dbURL + " doesn't contain a Rel database.");
    	} else if (msg.contains("RS0307:")) {
    		MessageDialog.openError(DbMain.getShell(), "Unable to open local database",
    				dbURL + " doesn't exist.");    		
    	} else
    		MessageDialog.openError(DbMain.getShell(), "Unable to open database",
    				msg);
    }
    
    private void doConnectionResultFailed(Throwable exception, String dbURL) {
    	doConnectionResultFailed(exception.toString(), dbURL);
    }
    
    /** Open a connection and associated panel. */
    private boolean openConnection(String dbURL, boolean permanent, boolean canCreate) {
    	if (DbMain.isNoLocalRel() && dbURL.startsWith("local:")) {
    		doConnectionResultFailed("Local Rel server is not installed.", dbURL);
    		return false;
    	}
    	AttemptConnectionResult result = attemptConnectionOpen(dbURL, canCreate);
    	if (result.client != null) {
    		doConnectionResultSuccess(result.client, dbURL, permanent);
    		return true;
    	} else {
    		doConnectionResultFailed(result.exception, dbURL);
    		return false;
    	}
    }
    
    /** Attempt to open a connection.  Return null if succeeded (!) and exception if failed. */
    private AttemptConnectionResult attemptConnectionOpen(String dbURL, boolean createAllowed) {
        DbMain.setStatus("Opening connection to " + dbURL);
        try {
        	StringReceiverClient client = ClientFromURL.openConnection(dbURL, createAllowed);
            return new AttemptConnectionResult(client);
        } catch (Throwable exception) {
        	return new AttemptConnectionResult(exception);
        }
    }
	
	public void addMode(String iconImageFilename, String toolTipText, ModeTabContent content) {
		addMode(ResourceManager.getPluginImage("RelUI", "icons/" + iconImageFilename), toolTipText, content);
	}
	
	public DbTab() {
		super(DbMain.getMainPanel().getTabFolder(), SWT.None);
		
		setImage(ResourceManager.getPluginImage("RelUI", "icons/plusIcon.png"));
		setToolTipText("New tab");
	}
	
	public void setText(String s) {
		super.setText(s);
		if (countModes() == 0) {
			setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
			setToolTipText(s);
			addMode("ModeRelIcon.png", "Rel", new DbTabContentRel());
			addMode("ModeRevIcon.png", "Rev", new DbTabContentRev());
			addMode("ModeCmdIcon.png", "Command line", new DbTabContentCmd());
			setMode(0);
		}
	}
	
	public void openDatabaseAtURI(String uri, boolean canCreate) {
		openConnection(uri, true, canCreate);
	}

	private void createDatabaseAtLocation() {
		openDatabaseAtURI(locationPanel.getDatabaseURI(), true);
	}
	
	private void openDatabaseAtLocation() {
		openDatabaseAtURI(locationPanel.getDatabaseURI(), false);
	}
	
	public void buildLocationPanel(TopPanel parent) {
		locationPanel = new LocationPanel(parent, SWT.None) {
			@Override
			public void notifyDatabaseURIModified() {
				openDatabaseAtLocation();
			}
		};
	}

	public void newDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI("local:" + string);
		createDatabaseAtLocation();
	}

	public void openLocalDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI("local:" + string);
		openDatabaseAtLocation();
	}

	public void openRemoteDatabase(String string) {
		setMode(0);
		locationPanel.setDatabaseURI(string);
		openDatabaseAtLocation();
	}
    
}
