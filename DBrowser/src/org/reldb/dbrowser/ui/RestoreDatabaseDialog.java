package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.poi.Version;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.Core;
import org.reldb.dbrowser.ui.crash.CrashTrap;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.connection.string.ClientLocal;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ProgressBar;

public class RestoreDatabaseDialog extends Dialog {
	
	private enum Mode {CREATEDB, LOADDB, FINISHED};
	
	private Mode mode = Mode.CREATEDB;
	
	private Shell shell;
	
	private Text textDatabaseDir;
	private Text textSourceFile;
	private StyledText textOutput;
	
	private DirectoryDialog newDatabaseDialog;
	private FileDialog restoreFileDialog;
	
	private ProgressBar progressBar;
	
	private Button btnDatabaseDir;
	private Button btnSourceFile;
	private Button btnCancel;
	private Button btnOk;
	
	private Color red;
	private Color black;
	private Color green;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RestoreDatabaseDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		setText("Create and Restore Database");
		
		newDatabaseDialog = new DirectoryDialog(parent);
		newDatabaseDialog.setText("Create Database");
		newDatabaseDialog.setMessage("Select a folder to hold a new database.");
		newDatabaseDialog.setFilterPath(System.getProperty("user.home"));
	
		restoreFileDialog = new FileDialog(Core.getShell(), SWT.OPEN);
		restoreFileDialog.setFilterPath(System.getProperty("user.home"));
		restoreFileDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
		restoreFileDialog.setFilterNames(new String[] {"Rel script", "All Files"});
		restoreFileDialog.setText("Load Backup");
	}

	/**
	 * Open the dialog.
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void setupUIAsFinished() {
		textDatabaseDir.setEnabled(false);
		textSourceFile.setEnabled(false);
		btnDatabaseDir.setEnabled(false);
		btnSourceFile.setEnabled(false);
		btnOk.setVisible(true);
		btnOk.setText("Open New Database");
		btnOk.requestLayout();
		btnCancel.setVisible(true);
		btnCancel.setText("Close");
		btnCancel.requestLayout();
		progressBar.setVisible(false);
		mode = Mode.FINISHED;
	}
	
	private void setupUIAsReload() {
		textDatabaseDir.setEnabled(false);
		textSourceFile.setEnabled(true);
		btnDatabaseDir.setEnabled(false);
		btnSourceFile.setEnabled(true);
		btnOk.setVisible(true);
		btnOk.setText("Reload");
		btnOk.requestLayout();
		btnCancel.setVisible(true);
		progressBar.setVisible(false);
		mode = Mode.LOADDB;
	}
	
	private void setupUIAsRunning() {
		textDatabaseDir.setEnabled(false);
		textSourceFile.setEnabled(false);
		btnDatabaseDir.setEnabled(false);
		btnSourceFile.setEnabled(false);
		btnOk.setVisible(false);
		btnCancel.setVisible(false);
		progressBar.setVisible(true);
	}
	
	private void output(String s, Color color) {
		shell.getDisplay().asyncExec(() -> {
			StyleRange styleRange = new StyleRange();
			styleRange.start = textOutput.getCharCount();
			styleRange.length = s.length();
			styleRange.fontStyle = SWT.NORMAL;
			styleRange.foreground = color;
			textOutput.append(s);
			textOutput.append("\n");
			textOutput.setStyleRange(styleRange);
			textOutput.setCaretOffset(textOutput.getCharCount());
			textOutput.setSelection(textOutput.getCaretOffset(), textOutput.getCaretOffset());
		});
	}

	private void doRestore(String dbDir, String backupToRestore) {
		if (mode == Mode.CREATEDB)
			output("Creating database...", green);
		else
			output("Opening database...", green);
		try (ClientLocal connection = new ClientLocal(dbDir, mode == Mode.CREATEDB, new CrashTrap(shell, Version.getVersion()), null)) {
			output(connection.getServerAnnouncement(), black);
			connection.sendExecute(backupToRestore);
			boolean loadFail = false;
			String lastError = "";
			String r;
			while ((r = connection.receive()) != null) {
				if (r.startsWith("ERROR:")) {
					output(r, red);
					lastError = r;
					loadFail = true;
					break;
				} else if (!r.equals("Ok.")) {
					output(r, black);
				}
			}
			if (loadFail) {
				output("\nLoad failed due to " + lastError, red);
				shell.getDisplay().asyncExec(() -> setupUIAsReload());
			} else {
				output("Done.", green);
				shell.getDisplay().asyncExec(() -> setupUIAsFinished());
			}
		} catch (Throwable e) {
			output("Error:\n" + e.getMessage(), red);			
		}
	}
	
	private void process(String dbDir, String backupToRestore) {
		setupUIAsRunning();
		Thread processor = new Thread() {
			public void run() {
				doRestore(dbDir, backupToRestore);								
			}
		};
		processor.start();
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		mode = Mode.CREATEDB;
		
		shell = new Shell(getParent(), getStyle());
		shell.setSize(640, 480);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		red = new Color(shell.getDisplay(), 200, 0, 0);
		black = new Color(shell.getDisplay(), 0, 0, 0);
		green = new Color(shell.getDisplay(), 0, 100, 0);
		
		Label lblDatabaseDir = new Label(shell, SWT.NONE);
		FormData fd_lblDatabaseDir = new FormData();
		lblDatabaseDir.setLayoutData(fd_lblDatabaseDir);
		lblDatabaseDir.setText("Directory for new database:");
		
		textDatabaseDir = new Text(shell, SWT.BORDER);
		FormData fd_textDatabaseDir = new FormData();
		textDatabaseDir.setLayoutData(fd_textDatabaseDir);
		
		btnDatabaseDir = new Button(shell, SWT.NONE);
		FormData fd_btnDatabaseDir = new FormData();
		btnDatabaseDir.setLayoutData(fd_btnDatabaseDir);
		btnDatabaseDir.setText("Directory...");
		btnDatabaseDir.addListener(SWT.Selection, e -> {
			if (textDatabaseDir.getText().trim().length() == 0)
				newDatabaseDialog.setFilterPath(System.getProperty("user.home"));
			else
				newDatabaseDialog.setFilterPath(textDatabaseDir.getText());
			String result = newDatabaseDialog.open();
			if (result == null)
				return;
			textDatabaseDir.setText(result);
		});
		
		Label lblSourceFile = new Label(shell, SWT.NONE);
		FormData fd_lblSourceFile = new FormData();
		lblSourceFile.setLayoutData(fd_lblSourceFile);
		lblSourceFile.setText("Backup to restore:");
		
		textSourceFile = new Text(shell, SWT.BORDER);
		FormData fd_textSourceFile = new FormData();
		textSourceFile.setLayoutData(fd_textSourceFile);
		
		btnSourceFile = new Button(shell, SWT.NONE);
		FormData fd_btnSourceFile = new FormData();
		btnSourceFile.setLayoutData(fd_btnSourceFile);
		btnSourceFile.setText("Choose file...");
		btnSourceFile.addListener(SWT.Selection, e -> {
			if (textSourceFile.getText().trim().length() == 0)
				restoreFileDialog.setFilterPath(System.getProperty("user.home"));
			else
				restoreFileDialog.setFilterPath(textSourceFile.getText());
			String result = restoreFileDialog.open();
			if (result == null)
				return;
			textSourceFile.setText(result);
		});

		progressBar = new ProgressBar(shell, SWT.INDETERMINATE);
		FormData fd_progressBar = new FormData();
		progressBar.setLayoutData(fd_progressBar);
		progressBar.setVisible(false);
				
		btnOk = new Button(shell, SWT.NONE);
		FormData fd_btnOk = new FormData();
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		btnOk.addListener(SWT.Selection, e -> {
			String databaseDir = textDatabaseDir.getText().trim();
			if (mode == Mode.FINISHED) {
				Core.openDatabase("db:" + databaseDir);
				shell.dispose();
				return;
			}
			if (databaseDir.length() == 0) {
				MessageDialog.openInformation(shell, "No Directory Specified", "No database directory was specified.");
				return;
			}
			String sourceFileName = textSourceFile.getText().trim();
			if (sourceFileName.length() == 0) {
				MessageDialog.openInformation(shell, "No Backup File Specified", "No database backup file to restore was specified.");
				return;
			}
			File sourceFile = new File(sourceFileName);
			if (!sourceFile.exists()) {
				MessageDialog.openInformation(shell, "Unable to Open Backup File", "Database backup file cannot be found.");
				return;
			}
			String backup = "";
			try {
				backup = new String(Files.readAllBytes(sourceFile.toPath()), "UTF-8");
			} catch (IOException e2) {
				MessageDialog.openInformation(shell, "Unable to Read Backup File", "The database backup file a can't be read due to " + e2.getMessage());
				return;
			}
			if (backup.trim().length() == 0) {
				MessageDialog.openInformation(shell, "Unable to Read Backup File", "The database backup file appears to be empty or unreadable.");
				return;
			}
			if (mode == Mode.LOADDB)
				process(databaseDir, backup);
			else {
				Connection connection;
				try {
					connection = new Connection("db:" + databaseDir);
					connection.close();
					MessageDialog.openInformation(shell, "Database Exists", "A Rel database already exists at " + databaseDir);
				} catch (Exception e1) {
					Throwable cause = e1.getCause();
					if (cause != null && cause.getMessage().startsWith("RS0406:"))
						process(databaseDir, backup);
					else
						MessageDialog.openError(shell, "Problem with Directory", "Unable to use " + databaseDir + " due to: " + e1.getMessage());
				}
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, e -> shell.dispose());
	
		Label lblOutput = new Label(shell, SWT.NONE);
		FormData fd_lblOutput = new FormData();
		lblOutput.setLayoutData(fd_lblOutput);
		lblOutput.setText("Output");
		
		textOutput = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		textOutput.setEditable(false);
		textOutput.setWordWrap(true);
		FormData fd_textOutput = new FormData();
		textOutput.setLayoutData(fd_textOutput);

		fd_lblDatabaseDir.top = new FormAttachment(0, 10);
		fd_lblDatabaseDir.left = new FormAttachment(0, 5);

		fd_lblSourceFile.top = new FormAttachment(lblDatabaseDir, 25);
		fd_lblSourceFile.right = new FormAttachment(lblDatabaseDir, 0, SWT.RIGHT);
		
		fd_textDatabaseDir.top = new FormAttachment(0, 10);
		fd_textDatabaseDir.left = new FormAttachment(lblDatabaseDir, 5);		
		fd_textDatabaseDir.right = new FormAttachment(btnSourceFile, -5);

		fd_textSourceFile.top = new FormAttachment(lblDatabaseDir, 25);
		fd_textSourceFile.left = new FormAttachment(lblSourceFile, 5);
		fd_textSourceFile.right = new FormAttachment(btnSourceFile, -5);
		
		fd_btnDatabaseDir.top = new FormAttachment(0, 10);
		fd_btnDatabaseDir.left = new FormAttachment(btnSourceFile, 0, SWT.LEFT);
		fd_btnDatabaseDir.right = new FormAttachment(btnSourceFile, 0, SWT.RIGHT);

		fd_btnSourceFile.top = new FormAttachment(lblDatabaseDir, 25);
		fd_btnSourceFile.right = new FormAttachment(100, -5);
		
		fd_progressBar.top = new FormAttachment(textSourceFile, 10);
//		fd_progressBar.bottom = new FormAttachment(lblOutput, -6);
		fd_progressBar.right = new FormAttachment(textOutput, 0, SWT.RIGHT);
		fd_progressBar.left = new FormAttachment(textOutput, 0, SWT.LEFT);
		
		fd_lblOutput.top = new FormAttachment(progressBar, 5);
		fd_lblOutput.left = new FormAttachment(0, 5);
		
		fd_textOutput.top = new FormAttachment(lblOutput, 5);
		fd_textOutput.left = new FormAttachment(0, 5);
		fd_textOutput.bottom = new FormAttachment(btnOk, -5);
		fd_textOutput.right = new FormAttachment(100, -5);
		
		fd_btnCancel.bottom = new FormAttachment(100, -5);
		fd_btnCancel.right = new FormAttachment(100, -5);
		
		fd_btnOk.bottom = new FormAttachment(100, -5);
		fd_btnOk.right = new FormAttachment(btnCancel, -5);
	}
}
