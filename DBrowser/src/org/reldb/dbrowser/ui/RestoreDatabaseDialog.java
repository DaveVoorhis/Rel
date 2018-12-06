package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.Core;
import org.reldb.rel.client.Connection;
import org.eclipse.swt.widgets.Button;

public class RestoreDatabaseDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text textDatabaseDir;
	private Text textSourceFile;
	private Text textOutput;
	private DirectoryDialog newDatabaseDialog;
	private FileDialog restoreFileDialog;
	
	private Button btnCancel;
	private Button btnOk;

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
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void process(String dbURL) {
		textOutput.append("Ready to create database " + dbURL + "\n");
		try (Connection connection = new Connection(dbURL, true)) {
			textOutput.append("Database " + dbURL + " created.\n");
			btnOk.setVisible(false);
			btnCancel.setText("Close");
		} catch (Exception e) {
			textOutput.append("Unable to create database " + dbURL + "\n");
			textOutput.append(e.getMessage());
		}
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(640, 480);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Label lblDatabaseDir = new Label(shell, SWT.NONE);
		FormData fd_lblDatabaseDir = new FormData();
		lblDatabaseDir.setLayoutData(fd_lblDatabaseDir);
		lblDatabaseDir.setText("Directory for new database:");
		
		textDatabaseDir = new Text(shell, SWT.BORDER);
		FormData fd_textDatabaseDir = new FormData();
		textDatabaseDir.setLayoutData(fd_textDatabaseDir);
		
		Button btnDatabaseDir = new Button(shell, SWT.NONE);
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
		
		Button btnSourceFile = new Button(shell, SWT.NONE);
		FormData fd_btnSourceFile = new FormData();
		btnSourceFile.setLayoutData(fd_btnSourceFile);
		btnSourceFile.setText("Choose file...");
		
		Button btnOk = new Button(shell, SWT.NONE);
		FormData fd_btnOk = new FormData();
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		btnOk.addListener(SWT.Selection, e -> {
			String databaseDir = textDatabaseDir.getText().trim();
			if (databaseDir.length() == 0) {
				MessageDialog.openInformation(shell, "No Directory Specified", "No database directory was specified.");
				return;
			}
			String dbURL = "db:" + databaseDir;
			Connection connection;
			try {
				connection = new Connection(dbURL);
				connection.close();
				MessageDialog.openInformation(shell, "Database Exists", "A Rel database already exists at " + dbURL);
			} catch (Exception e1) {
				Throwable cause = e1.getCause();
				if (cause != null && cause.getMessage().startsWith("RS0406:"))
					process(dbURL);
				else
					MessageDialog.openError(shell, "Problem with Directory", "Unable to use " + dbURL + " due to: " + e1.getMessage());
			}			
		});
		
		Button btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, e -> shell.dispose());
	
		Label lblOutput = new Label(shell, SWT.NONE);
		FormData fd_lblOutput = new FormData();
		lblOutput.setLayoutData(fd_lblOutput);
		lblOutput.setText("Output");
		
		textOutput = new Text(shell, SWT.BORDER | SWT.MULTI);
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
		
		fd_lblOutput.top = new FormAttachment(textSourceFile, 30);
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
