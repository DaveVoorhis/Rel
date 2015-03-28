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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

import org.reldb.rel.client.connection.string.ClientFromURL;
import org.reldb.rel.client.connection.string.StringReceiverClient;

import org.reldb.relui.dbui.crash.CrashTrap;
import org.reldb.relui.version.Version;

public class DbTab extends CTabItem {
	
	private Text textDbLocation;
	private String status = "";
	private AttemptConnectionResult connection = null;	
	private String lastURI = "";
	private String oldText = "";
    private CrashTrap crashTrap;
    private StringBuffer initialServerResponse = new StringBuffer();
    
    private DbTabContentCmd contentCmd = null;
    private DbTabContentRel contentRel = null;
    private DbTabContentRev contentRev = null;
    
    private ToolBar toolBarMode;
    private ToolItem tltmModeRel;
    private ToolItem tltmModeRev;
    private ToolItem tltmModeCmd;
    
    private Composite modeContent;
    private StackLayout contentStack;
    
	public DbTab() {
		super(DbMain.getTabFolder(), SWT.None);
		
		setImage(ResourceManager.getPluginImage("RelUI", "icons/plusIcon.png"));
		
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
		
		ToolItem tltmNewLocalDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmNewLocalDb.setToolTipText("New local database");
		tltmNewLocalDb.setImage(ResourceManager.getPluginImage("RelUI", "icons/NewDBIcon.png"));
		tltmNewLocalDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DbMain.newDatabase();
			}
		});
		
		ToolItem tltmOpenLocalDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenLocalDb.setToolTipText("Open local database");
		tltmOpenLocalDb.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBLocalIcon.png"));
		tltmOpenLocalDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DbMain.openLocalDatabase();
			}
		});
		
		ToolItem tltmOpenRemoteDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenRemoteDb.setToolTipText("Open remote database");
		tltmOpenRemoteDb.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBRemoteIcon.png"));
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
		tltmModeRel.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRelIcon.png"));
		tltmModeRel.setToolTipText("Rel");
		tltmModeRel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showRel();
			}
		});
		
		tltmModeRev = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeRev.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeRevIcon.png"));
		tltmModeRev.setToolTipText("Rev");
		tltmModeRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showRev();
			}
		});
		
		tltmModeCmd = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeCmd.setImage(ResourceManager.getPluginImage("RelUI", "icons/ModeCmdIcon.png"));
		tltmModeCmd.setToolTipText("Command line");
		tltmModeCmd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showCmd();
			}
		});
		
		setControl(core);
	}

	private void showRel() {
		if (contentRel == null)
			contentRel = new DbTabContentRel(DbTab.this, modeContent);
		contentStack.topControl = contentRel;
		modeContent.layout();
	}

	private void showRev() {
		if (contentRev == null)
			contentRev = new DbTabContentRev(DbTab.this, modeContent);
		contentStack.topControl = contentRev;
		modeContent.layout();		
	}
	
	private void showCmd() {
		if (contentCmd == null) 
			contentCmd = new DbTabContentCmd(DbTab.this, modeContent);
		contentStack.topControl = contentCmd;
		modeContent.layout();		
	}
	
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
    
    private static String wrapped(String s) {
    	if (s.length() < 80)
    		return s;
    	return s.replace(": ",":\n");
    }
    
    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
		setImage(ResourceManager.getPluginImage("RelUI", "icons/DatabaseIcon.png"));
		
		initialServerResponse = new StringBuffer();
		String r;
		try {
			while ((r = client.receive()) != null)
				if (!r.equals("Ok.")) {
					initialServerResponse.append(r);
					initialServerResponse.append('\n');
				}
		} catch (IOException e) {
        	MessageDialog.openError(DbMain.getShell(), "Database Access Problem",
        		wrapped("An error occured whilst establishing contact with " + dbURL + ":" + e));
		}
		
		crashTrap.setServerInitialResponse(initialServerResponse.toString());

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
        	tltmModeRel.setSelection(true);
        	showRel();
        }

		DbMain.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				if (event.item == DbTab.this)
					DbTab.this.close();
			}
		});
    }
    
    public String getInitialServerResponse() {
    	return initialServerResponse.toString();
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
    		crashTrap = new CrashTrap(this.getParent().getShell(), Version.getVersion());
    		StringReceiverClient client = ClientFromURL.openConnection(dbURL, createAllowed, crashTrap);
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
	
	public StringReceiverClient getConnection() {
		return connection.client;
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
		System.out.println("DbTab: makeBackup");
	}

}
