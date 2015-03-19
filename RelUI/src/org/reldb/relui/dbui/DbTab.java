package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.rel.client.string.ClientFromURL;
import org.reldb.rel.client.string.StringReceiverClient;
import org.reldb.relui.tools.ModeTab;
import org.reldb.relui.tools.ModeTabContent;
import org.reldb.relui.tools.TopPanel;

public class DbTab extends ModeTab {
	
	private LocationPanel locationPanel;
	private String status = "";
	private AttemptConnectionResult connection = null;	
	private String lastURI = "";
    
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
    
    private static String wrapped(String s) {
    	if (s.length() < 80)
    		return s;
    	return s.replace(": ",":\n");
    }
    
    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
		if (countModes() == 0) {
			setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
			addMode("ModeRelIcon.png", "Rel", new DbTabContentRel(this));
			addMode("ModeRevIcon.png", "Rev", new DbTabContentRev(this));
			addMode("ModeCmdIcon.png", "Command line", new DbTabContentCmd(this));
			setMode(0);
		}
        setStatus("Ok");
		DbMain.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				if (event.item == DbTab.this)
					DbTab.this.close();
			}
		});
    }
    
    public StringReceiverClient getConnection() {
    	if (connection != null)
    		return connection.client;
    	return null;
    }
    
    public String getStatus() {
    	return status;
    }
    
    public void setStatus(String s) {
    	status = s;
    	DbMain.setStatus(status);
    }
    
    private void doConnectionResultFailed(String reason, String dbURL) {
    	String shortMsg = "Unable to establish connection to " + dbURL;
        setStatus(shortMsg);
    	String msg = shortMsg + " - " + reason;
        msg = wrapped(msg);
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
    
    /** Attempt to open a connection.  Return null if succeeded (!) and exception if failed. */
    private AttemptConnectionResult attemptConnectionOpen(String dbURL, boolean createAllowed) {
        setStatus("Opening connection to " + dbURL);
        try {
        	StringReceiverClient client = ClientFromURL.openConnection(dbURL, createAllowed);
            return new AttemptConnectionResult(client);
        } catch (Throwable exception) {
        	return new AttemptConnectionResult(exception);
        }
    }
    
    /** Open a connection and associated panel. */
    private boolean openConnection(String dbURL, boolean permanent, boolean canCreate) {
		setText(dbURL);
    	if (DbMain.isNoLocalRel() && dbURL.startsWith("local:")) {
    		doConnectionResultFailed("Local Rel server is not installed.", dbURL);
    		return false;
    	}
    	connection = attemptConnectionOpen(dbURL, canCreate);
    	if (connection.client != null) {
    		doConnectionResultSuccess(connection.client, dbURL, permanent);
    		return true;
    	} else {
    		doConnectionResultFailed(connection.exception, dbURL);
    		return false;
    	}
    }
	
	public void addMode(String iconImageFilename, String toolTipText, ModeTabContent content) {
		addMode(ResourceManager.getPluginImage("RelUI", "icons/" + iconImageFilename), toolTipText, content);
	}
	
	public DbTab() {
		super(DbMain.getTabFolder(), SWT.None);
		
		setImage(ResourceManager.getPluginImage("RelUI", "icons/plusIcon.png"));
		setToolTipText("New tab");
	}
	
	public void setText(String s) {
		if (getText() == null || getText().length() == 0)
			new DbTab();
		super.setText(s);
		setToolTipText(s);
	}
	
	public void openDatabaseAtURI(String uri, boolean canCreate) {
		lastURI = uri;
		setShowClose(true);
		openConnection(uri, true, canCreate);
	}
	
	public void close() {
		if (connection != null && connection.client != null) {
			try {
				connection.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearModes();
			setImage(ResourceManager.getPluginImage("RelUI", "icons/plusIcon.png"));
		}
	}
	
	public void buildLocationPanel(TopPanel parent) {
		locationPanel = new LocationPanel(parent, SWT.None) {
			@Override
			public void notifyDatabaseURIModified() {
				if (locationPanel.getDatabaseURI().trim().length() == 0)
					locationPanel.setDatabaseURI(lastURI);
				else {
					close();
					openDatabaseAtURI(locationPanel.getDatabaseURI(), false);
				}
			}
		};
	}

	public void newDatabase(String string) {
		close();
		locationPanel.setDatabaseURI("local:" + string);
		openDatabaseAtURI(locationPanel.getDatabaseURI(), true);
	}

	public void openLocalDatabase(String string) {
		close();
		locationPanel.setDatabaseURI("local:" + string);
		openDatabaseAtURI(locationPanel.getDatabaseURI(), false);
	}

	public void openRemoteDatabase(String string) {
		close();
		locationPanel.setDatabaseURI(string);
		openDatabaseAtURI(locationPanel.getDatabaseURI(), false);
	}

	public void makeBackup() {
		System.out.println("DbTab: makeBackup");
	}
    
}
