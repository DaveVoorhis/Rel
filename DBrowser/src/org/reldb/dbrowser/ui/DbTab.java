package org.reldb.dbrowser.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
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
import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.IconLoader;
import org.reldb.dbrowser.MenuItemWithToolbar;
import org.reldb.dbrowser.ui.backup.Backup;
import org.reldb.dbrowser.ui.content.cmd.DbTabContentCmd;
import org.reldb.dbrowser.ui.content.conversion.DbTabContentConversion;
import org.reldb.dbrowser.ui.content.rel.DbTabContentRel;
import org.reldb.dbrowser.ui.content.rev.DbTabContentRev;
import org.reldb.dbrowser.ui.crash.CrashTrap;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.dbrowser.ui.version.Version;
import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class DbTab extends CTabItem {

	private Text textDbLocation;
	private String status = "";
	private AttemptConnectionResult connection = null;
	private String lastURI = "";
	private String oldText = "";

	private CrashTrap crashTrap;

	private DbTabContentCmd contentCmd = null;
	private DbTabContentRel contentRel = null;
	private DbTabContentRev contentRev = null;
	private DbTabContentConversion contentConversion = null;

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
		super(DBrowser.getTabFolder(), SWT.None);

		crashTrap = new CrashTrap(this.getParent().getShell(), Version.getVersion());

		setImage(IconLoader.loadIcon("plusIcon"));

		Composite core = new Composite(DBrowser.getTabFolder(), SWT.None);
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
		tltmNewLocalDb.addListener(SWT.Selection, e -> DBrowser.newDatabase());

		tltmOpenLocalDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenLocalDb.setToolTipText("Open local database");
		tltmOpenLocalDb.addListener(SWT.Selection, e -> DBrowser.openLocalDatabase());

		tltmOpenRemoteDb = new ToolItem(toolBarDatabase, SWT.NONE);
		tltmOpenRemoteDb.setToolTipText("Open remote database");
		tltmOpenRemoteDb.addListener(SWT.Selection, e -> DBrowser.openRemoteDatabase());

		textDbLocation = new Text(compDbLocation, SWT.BORDER);
		textDbLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		textDbLocation.addListener(SWT.KeyUp, e -> {
			if (e.character == 0xD && !textDbLocation.getText().trim().equals(oldText)) {
				oldText = textDbLocation.getText().trim();
				if (textDbLocation.getText().trim().length() == 0)
					textDbLocation.setText(lastURI);
				else {
					openDatabaseAtURI(textDbLocation.getText(), false);
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
		tltmModeRel.addListener(SWT.Selection, e -> showRel());

		tltmModeRev = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeRev.setToolTipText("Rev");
		tltmModeRev.addListener(SWT.Selection, e -> showRev());

		tltmModeCmd = new ToolItem(toolBarMode, SWT.RADIO);
		tltmModeCmd.setToolTipText("Command line");
		tltmModeCmd.addListener(SWT.Selection, e -> showCmd());

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

		core.pack();
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
		MenuItemWithToolbar.clear();
		boolean existing = true;
		if (contentRel == null) {
			existing = false;
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
		if (existing)
			contentRel.redisplayed();
		contentRel.activateMenu();
	}

	private void showRev() {
		MenuItemWithToolbar.clear();
		boolean existing = true;
		if (contentRev == null) {
			existing = false;
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
		if (existing)
			contentRev.redisplayed();
		contentRev.activateMenu();
	}

	private void showCmd() {
		MenuItemWithToolbar.clear();
		boolean existing = true;
		if (contentCmd == null) {
			existing = false;
			Cursor oldCursor = getParent().getCursor();
			getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT));
			try {
				contentCmd = new DbTabContentCmd(DbTab.this, modeContent);
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
			} catch (Exception e) {
				getParent().getCursor().dispose();
				getParent().setCursor(oldCursor);
				e.printStackTrace();
				MessageDialog.openError(DBrowser.getShell(), "Unable to open local database",
						wrapped("Unable to open command line due to error: " + e.toString()));
				return;
			}
		}
		contentStack.topControl = contentCmd;
		modeContent.layout();
		if (existing)
			contentCmd.redisplayed();
	}

	private void showConversion(String message, String dbURL) {
		Cursor oldCursor = getParent().getCursor();
		getParent().setCursor(new Cursor(getParent().getDisplay(), SWT.CURSOR_WAIT));
		try {
			contentConversion = new DbTabContentConversion(DbTab.this, message, dbURL, modeContent);
		} finally {
			getParent().getCursor().dispose();
			getParent().setCursor(oldCursor);
		}
		contentStack.topControl = contentConversion;
		modeContent.layout();
	}

	private static class AttemptConnectionResult {
		Throwable exception;
		DbConnection client;
		String dbURL;

		public AttemptConnectionResult(Throwable exception) {
			this.exception = exception;
			this.client = null;
			this.dbURL = null;
		}

		public AttemptConnectionResult(String dbURL, DbConnection client) {
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
		if (contentConversion != null) {
			contentConversion.dispose();
			contentConversion = null;
		}
		toolBarMode.setEnabled(false);
	}

	static String wrapped(String s) {
		if (s.length() < 80)
			return s;
		return s.replace(": ", ":\n");
	}

	private void doConnectionResultConversion(String message, String dbURL) {
		setImage(IconLoader.loadIcon("DatabaseIcon"));

		setStatus("Requires conversion to the current database format.");

		showConversion(message, dbURL.substring("db:".length()));

		DBrowser.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				if (event.item == DbTab.this)
					DbTab.this.close();
			}
		});

		DBrowser.createNewTabIfNeeded();
	}

	private void doConnectionResultSuccess(DbConnection client, String dbURL, boolean permanent) {
		setImage(IconLoader.loadIcon("DatabaseIcon"));

		setStatus("Ok");
		toolBarMode.setEnabled(true);

		refresh();

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

		DBrowser.getTabFolder().addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				if (event.item == DbTab.this)
					DbTab.this.close();
			}
		});

		DBrowser.createNewTabIfNeeded();

		DBrowser.updateRecentlyUsedDatabaseList(dbURL);
	}

	public void refresh() {
		tltmModeRev.setEnabled(connection.client.hasRevExtensions() >= 0);
	}

	public void switchToCmdMode() {
		tltmModeRev.setSelection(false);
		tltmModeRel.setSelection(false);
		tltmModeCmd.setSelection(true);
		showCmd();
	}

	public void setAndDisplayCmdContent(String content) {
		switchToCmdMode();
		contentCmd.setContent(content);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String s) {
		status = s;
		DBrowser.setStatus(status);
	}

	private void doConnectionResultFailed(Throwable reason, String dbURL) {
		String shortMsg = "Unable to establish connection to " + dbURL;
		setStatus(shortMsg);
		String msg = shortMsg + " - " + reason;
		msg = wrapped(msg);
		if (msg.contains("The environment cannot be locked for single writer access. ENV_LOCKED")) {
			MessageDialog.openError(DBrowser.getShell(), "Unable to open local database",
					"A copy of Rel is already accessing the database you're trying to open at " + dbURL);
		} else if (msg.contains("Connection refused")) {
			MessageDialog.openError(DBrowser.getShell(), "Unable to open remote database",
					"A Rel server doesn't appear to be running or available at " + dbURL);
		} else if (msg.contains("RS0406:")) {
			MessageDialog.openError(DBrowser.getShell(), "Unable to open local database",
					dbURL + " either doesn't exist or doesn't contain a Rel database.");
		} else if (msg.contains("RS0307:")) {
			MessageDialog.openError(DBrowser.getShell(), "Unable to open local database", dbURL + " doesn't exist.");
		} else if (msg.contains("RS0410:")) {
			if (reason instanceof DatabaseFormatVersionException) {
				DatabaseFormatVersionException dfve = (DatabaseFormatVersionException) reason;
				if (dfve.getOldVersion() >= 0)
					doConnectionResultConversion("Database " + dbURL + " needs to be converted to the current format.",
							dbURL);
				else
					MessageDialog.openError(DBrowser.getShell(), "Unable to open local database", "The database at "
							+ dbURL + " appears to be a newer format than that supported by this version of Rel.");
			} else
				MessageDialog.openError(DBrowser.getShell(), "Unable to open local database", "You'll need to open "
						+ dbURL
						+ " in the version of Rel last used to access it, back it up, and import the backup into a new database.");
		} else
			MessageDialog.openError(DBrowser.getShell(), "Unable to open database", msg);
	}

	private AttemptConnectionResult openConnection(String dbURL, boolean createAllowed) {
		try {
			DbConnection client = new DbConnection(dbURL, createAllowed, crashTrap);
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
			if (DBrowser.isNoLocalRel() && dbURL.startsWith("db:")) {
				doConnectionResultFailed(new Throwable("Local Rel server is not installed."), dbURL);
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
		clearModes();
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}

	public boolean openDatabaseAtURI(String uri, boolean canCreate) {
		close();
		textDbLocation.setText(uri);
		lastURI = uri;
		if (openConnection(uri, true, canCreate)) {
			setShowClose(true);
			return true;
		}
		return false;
	}

	public boolean isOpenOnADatabase() {
		if (connection == null)
			return false;
		return (connection.client != null
				|| (connection.exception != null && connection.exception instanceof DatabaseFormatVersionException));
	}

	public String getURL() {
		if (isOpenOnADatabase())
			return connection.dbURL;
		return null;
	}

	public DbConnection getConnection() {
		if (!isOpenOnADatabase())
			return null;
		return connection.client;
	}

	public void close() {
		if (connection != null && connection.client != null) {
			clearModes();
			setImage(IconLoader.loadIcon("plusIcon"));
		}
	}

	public boolean newDatabase(String string) {
		return openDatabaseAtURI("db:" + string, true);
	}

	public boolean openLocalDatabase(String string) {
		return openDatabaseAtURI("db:" + string, false);
	}

	public boolean openRemoteDatabase(String string) {
		return openDatabaseAtURI(string, false);
	}

	public boolean openDefaultDatabase(String string) {
		return openDatabaseAtURI("db:" + string, true);
	}

	public void makeBackup() {
		Backup.makeBackup(connection.client);
	}

	public CrashHandler getCrashHandler() {
		return crashTrap;
	}

	public void openFile(String fname) {
		showCmd();
		contentCmd.load(fname);
	}

}
