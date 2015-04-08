package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.rel.client.connection.string.ClientFromURL;
import org.reldb.rel.client.connection.string.StringReceiverClient;
import org.reldb.relui.dbui.crash.CrashTrap;
import org.reldb.relui.dbui.preferences.PreferenceChangeAdapter;
import org.reldb.relui.dbui.preferences.PreferenceChangeEvent;
import org.reldb.relui.dbui.preferences.PreferenceChangeListener;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;
import org.reldb.relui.dbui.preferences.Preferences;
import org.reldb.relui.version.Version;

public class DbTab extends CTabItem {
	
	private Text textDbLocation;
	private String status = "";
	private AttemptConnectionResult connection = null;	
	private String lastURI = "";
	private String oldText = "";
    
    private DbTabContentCmd contentCmd = null;
    private DbTabContentRel contentRel = null;
    private DbTabContentRev contentRev = null;
    
    private ToolBar toolBarMode;
    
	private ToolItem tltmOpenRemoteDb;
	private ToolItem tltmOpenLocalDb;
	private ToolItem tltmNewLocalDb;
    private ToolItem tltmModeRel;
    private ToolItem tltmModeRev;
    private ToolItem tltmModeCmd;
    
    private Composite modeContent;
    private StackLayout contentStack;
    
    private PreferenceChangeListener preferenceChangeListener;
    
	public DbTab() {
		super(DbMain.getTabFolder(), SWT.None);
		
		setImage(IconLoader.loadIcon("plusIcon"));
		
		Composite core = new Composite(DbMain.getTabFolder(), SWT.None);
		core.setLayout(new FormLayout());
		
		CBanner bannerDbLocationMode = new CBanner(core, SWT.NONE);
		FormData fd_bannerDbLocationMode = new FormData();
		fd_bannerDbLocationMode.right = new FormAttachment(100);
		fd_bannerDbLocationMode.top = new FormAttachment(0);
		fd_bannerDbLocationMode.left = new FormAttachment(0);
		bannerDbLocationMode.setLayoutData(fd_bannerDbLocationMode);
		
		Composite compDbLocation = new Composite(bannerDbLocationMode, SWT.NONE);
		bannerDbLocationMode.setLeft(compDbLocation);
		GridLayout gl_compDbLocation = new GridLayout(2, false);
		gl_compDbLocation.verticalSpacing = 0;
		gl_compDbLocation.marginWidth = 0;
		gl_compDbLocation.marginHeight = 0;
		compDbLocation.setLayout(gl_compDbLocation);
		
		ToolBar toolBarDatabase = new ToolBar(compDbLocation, SWT.None);
		
		tltmNewLocalDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmNewLocalDb.setToolTipText("Create or open local database");
		tltmNewLocalDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DbMain.newDatabase();
			}
		});
		
		tltmOpenLocalDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenLocalDb.setToolTipText("Open local database");
		tltmOpenLocalDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DbMain.openLocalDatabase();
			}
		});
		
		tltmOpenRemoteDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenRemoteDb.setToolTipText("Open remote database");
		tltmOpenRemoteDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DbMain.openRemoteDatabase();
			}
		});
		
		textDbLocation = new Text(compDbLocation, SWT.BORDER);
		textDbLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		textDbLocation.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == 0xD && !textDbLocation.getText().trim().equals(oldText)) {
					oldText = textDbLocation.getText().trim();
					if (textDbLocation.getText().trim().length() == 0)
						textDbLocation.setText(lastURI);
					else {
						close();
						openDatabaseAtURI(textDbLocation.getText(), false);
					}
				}
			}	
		});
		
		toolBarMode = new ToolBar(bannerDbLocationMode, SWT.None);
		toolBarMode.setEnabled(false);
		bannerDbLocationMode.setRight(toolBarMode);
		
		modeContent = new Composite(core, SWT.NONE);
		contentStack = new StackLayout();
		modeContent.setLayout(contentStack);
		FormData fd_modeContent = new FormData();
		fd_modeContent.bottom = new FormAttachment(100);
		fd_modeContent.top = new FormAttachment(bannerDbLocationMode);
		fd_modeContent.right = new FormAttachment(100);
		fd_modeContent.left = new FormAttachment(0);
		modeContent.setLayoutData(fd_modeContent);
		
		tltmModeRel = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeRel.setToolTipText("Rel");
		tltmModeRel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showRel();
			}
		});
		
		tltmModeRev = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeRev.setToolTipText("Rev");
		tltmModeRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showRev();
			}
		});
		
		tltmModeCmd = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeCmd.setToolTipText("Command line");
		tltmModeCmd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showCmd();
			}
		});
		
		setControl(core);
		
		setupIcons();
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTab") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
				if (connection != null && connection.client != null)
					setImage(IconLoader.loadIcon("DatabaseIcon"));
				else
					setImage(IconLoader.loadIcon("plusIcon"));
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

    private void setupIcons() {
		tltmNewLocalDb.setImage(IconLoader.loadIcon("NewDBIcon"));
		tltmOpenLocalDb.setImage(IconLoader.loadIcon("OpenDBLocalIcon"));
		tltmOpenRemoteDb.setImage(IconLoader.loadIcon("OpenDBRemoteIcon"));
		tltmModeRel.setImage(IconLoader.loadIcon("ModeRelIcon"));
		tltmModeRev.setImage(IconLoader.loadIcon("ModeRevIcon"));
		tltmModeCmd.setImage(IconLoader.loadIcon("ModeCmdIcon"));
	}

	private void showRel() {
		if (contentRel == null) {
			Cursor oldCursor = getParent().getCursor();
			getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT)); 
			try {
				contentRel = new DbTabContentRel(DbTab.this, modeContent);
			} finally {
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
			}
		}
		contentStack.topControl = contentRel;
		modeContent.layout();
	}

	private void showRev() {
		if (contentRev == null) {
			Cursor oldCursor = getParent().getCursor();
			getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT)); 
			try {
				contentRev = new DbTabContentRev(DbTab.this, modeContent);
			} finally {
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
			}
		}
		contentStack.topControl = contentRev;
		modeContent.layout();		
	}
	
	private void showCmd() {
		if (contentCmd == null) {
			Cursor oldCursor = getParent().getCursor();
			getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT)); 
			try {
				contentCmd = new DbTabContentCmd(DbTab.this, modeContent);
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
			} catch (Exception e) {
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
	        	MessageDialog.openError(DbMain.getShell(), "Unable to open local database",
	        			wrapped("Unable to open command line due to error: " + e.toString()));
	        	return;
			}
		}
		contentStack.topControl = contentCmd;
		modeContent.layout();		
	}
	
    private static class AttemptConnectionResult {
    	Throwable exception;
    	StringReceiverClient client;
    	String dbURL;
    	public AttemptConnectionResult(Throwable exception) {
    		this.exception = exception;
    		this.client = null;
    		this.dbURL = null;
    	}
    	public AttemptConnectionResult(String dbURL, StringReceiverClient client) {
    		this.exception = null;
    		this.client = client;
    		this.dbURL = dbURL;
    	}
    }
    
    private void clearModes() {
    	if (contentCmd != null) {
    		contentCmd.dispose();
    		contentCmd = null;
    	}
    	if (contentRel != null) {
    		contentRel.dispose();
    		contentRel = null;
    	}
    	if (contentRev != null) {
    		contentRev.dispose();
    		contentRev = null;
    	}
    	toolBarMode.setEnabled(false);
    }
    
    static String wrapped(String s) {
    	if (s.length() < 80)
    		return s;
    	return s.replace(": ",":\n");
    }
    
    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
		setImage(IconLoader.loadIcon("DatabaseIcon"));

        setStatus("Ok");
        toolBarMode.setEnabled(true);

        if (tltmModeRel.getSelection())
        	showRel();
        else if (tltmModeRev.getSelection())
        	showRev();
        else if (tltmModeCmd.getSelection())
        	showCmd();
        else {
        	// default mode
        	if (Preferences.getPreferenceBoolean(PreferencePageGeneral.DEFAULT_CMD_MODE)) {
        		tltmModeCmd.setSelection(true);
        		showCmd();
        	} else {
	        	tltmModeRel.setSelection(true);
	        	showRel();
        	}
        }

		DbMain.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				if (event.item == DbTab.this)
					DbTab.this.close();
			}
		});
		
		DbMain.createNewTabIfNeeded();
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
    
    private AttemptConnectionResult openConnection(String dbURL, boolean createAllowed) {
        try {
    		CrashTrap crashTrap = new CrashTrap(this.getParent().getShell(), Version.getVersion());
    		StringReceiverClient client = ClientFromURL.openConnection(dbURL, createAllowed, crashTrap);
    		return new AttemptConnectionResult(dbURL, client);
        } catch (Throwable exception) {
        	return new AttemptConnectionResult(exception);
        }
    }
    
    /** Attempt to open a connection. */
    private AttemptConnectionResult attemptConnectionOpen(String dbURL, boolean createAllowed) {
        setStatus("Opening connection to " + dbURL);
        return openConnection(dbURL, createAllowed);
    }
	
    /** Open a connection and associated panel. */
    private boolean openConnection(String dbURL, boolean permanent, boolean canCreate) {
		Cursor oldCursor = getParent().getCursor();
		getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT)); 
    	try {
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
    	} finally {
			getParent().getCursor().dispose();
			getParent().setCursor(oldCursor);
    	}
    }

	public void dispose() {
    	if (connection != null && connection.client != null)
			try {
				connection.client.close();
			} catch (IOException e) {
			}
    	clearModes();
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
    	super.dispose();
    }

	public void openDatabaseAtURI(String uri, boolean canCreate) {
		lastURI = uri;
		setShowClose(true);
		openConnection(uri, true, canCreate);
	}

	public boolean isOpenOnADatabase() {
		if (connection == null)
			return false;
		return (connection.client != null);
	}

	public String getURL() {
		if (isOpenOnADatabase())
			return connection.dbURL;
		return null;
	}
	
	public void close() {
		if (connection != null && connection.client != null) {
			try {
				connection.client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearModes();
			setImage(IconLoader.loadIcon("plusIcon"));
		}
	}
	
	public void newDatabase(String string) {
		close();
		textDbLocation.setText("local:" + string);
		openDatabaseAtURI(textDbLocation.getText(), true);
	}

	public void openLocalDatabase(String string) {
		close();
		textDbLocation.setText("local:" + string);
		openDatabaseAtURI(textDbLocation.getText(), false);
	}

	public void openRemoteDatabase(String string) {
		close();
		textDbLocation.setText(string);
		openDatabaseAtURI(textDbLocation.getText(), false);
	}

	public void openDefaultDatabase(String string) {
		close();
		textDbLocation.setText("local:" + string);
		openDatabaseAtURI(textDbLocation.getText(), true);		
	}

	public void makeBackup() {
		// TODO
		System.out.println("DbTab: makeBackup");
	}

}
