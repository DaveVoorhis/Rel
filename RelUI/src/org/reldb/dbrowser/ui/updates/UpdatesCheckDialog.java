package org.reldb.dbrowser.ui.updates;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.updates.UpdatesCheck.SendStatus;

public class UpdatesCheckDialog extends Dialog {
	
	private Label lblNewUpdatesAvailable;
	private Label lblNewUpdateURL;
	private Label lblInstructions;
	private Label lblProgress;
	private Button btnCancel;
	private Button btnGo;
	private ProgressBar progressBar;
	
	private Shell shlUpdatesCheck;
	
	private UpdatesCheck checker;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UpdatesCheckDialog(Shell parent, int style) {
		super(parent, SWT.NONE);
		setText("Check for Updates");
		shlUpdatesCheck = createContents();
		checker = new UpdatesCheck(btnGo, lblProgress, progressBar) {
			@Override
		    public void completed(SendStatus sendStatus) {
				UpdatesCheckDialog.this.completed(sendStatus);
			}
			@Override
			public void quit() {
				UpdatesCheckDialog.this.quit();
			}
		};
	}
	
	protected void completed(SendStatus sendStatus) {
		String failHeading = "Check for Updates Failed";
		try {
			if (sendStatus.getResponse() != null && sendStatus.getResponse().startsWith("Success")) {
				String updateURL = UpdatesCheck.getUpdateURL(sendStatus);
				if (updateURL == null)
					lblNewUpdatesAvailable.setText("No new updates available.");
				else {
					lblNewUpdateURL.setVisible(true);
					lblNewUpdateURL.setText(updateURL);
				}
				lblNewUpdatesAvailable.setVisible(true);
				btnGo.setText("Exit");
				btnCancel.setVisible(false);
				lblProgress.setVisible(false);
				progressBar.setVisible(false);
				lblInstructions.setVisible(false);
	        } else
	        	if (sendStatus.getException() != null) {
        			sendStatus.getException().printStackTrace();
	        		MessageDialog.openError(getParent(), failHeading, "Unable to send request: " + sendStatus.getException().toString());
	        	} else
	        		MessageDialog.openError(getParent(), failHeading, "Unable to send request: " + sendStatus.getResponse());
		} catch (Exception e1) {
    		String exceptionName = e1.getClass().getName().toString(); 
    		if (exceptionName.equals("java.lang.InterruptedException"))
    			MessageDialog.openError(getParent(), failHeading, "Check for Updates Cancelled");
    		else {
    			e1.printStackTrace();
    			MessageDialog.openError(getParent(), failHeading, "Unable to send request: " + e1.toString());
    		}
		}
		lblProgress.setText("Ready...");
	}

	protected void open() {
		shlUpdatesCheck.open();
		shlUpdatesCheck.layout();
		Display display = getParent().getDisplay();
		while (!shlUpdatesCheck.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}
	
	/**
	 * Launch the dialog.
	 * @param shell
	 */
	public static void launch(Shell shell) {
		try {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					UpdatesCheckDialog checker = new UpdatesCheckDialog(shell, SWT.None);
					checker.open();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doSend() {
		if (lblNewUpdatesAvailable.isVisible())
			quit();
		else
			checker.doSend();
	}
	
	protected void doCancel() {
		checker.doCancel();
	}
	
	protected void quit() {
		shlUpdatesCheck.dispose();
	}
	
	/** Create contents of the dialog. */
	protected Shell createContents() {
		Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setSize(600, 250);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Composite panelIntro = new Composite(shell, SWT.NONE);
		panelIntro.setLayout(new GridLayout(2, false));
		FormData fd_panelIntro = new FormData();
		fd_panelIntro.top = new FormAttachment(0);
		fd_panelIntro.right = new FormAttachment(100);
		fd_panelIntro.left = new FormAttachment(0);
		panelIntro.setLayoutData(fd_panelIntro);
		
		Label lblIcon = new Label(panelIntro, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblIcon.setImage(IconLoader.loadIcon("RelIcon"));
		
		lblInstructions = new Label(panelIntro, SWT.WRAP);
		lblInstructions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblInstructions.setText("Press the Check for Updates button to check for updates.");
		
		lblNewUpdatesAvailable = new Label(shell, SWT.WRAP);
		lblNewUpdatesAvailable.setText("New Update Available. Click to download:");
		lblNewUpdatesAvailable.setVisible(false);
		FormData fd_lblNewUpdatesAvailable = new FormData();
		fd_lblNewUpdatesAvailable.top = new FormAttachment(panelIntro);
		fd_lblNewUpdatesAvailable.left = new FormAttachment(10);
		fd_lblNewUpdatesAvailable.right = new FormAttachment(100);
		lblNewUpdatesAvailable.setLayoutData(fd_lblNewUpdatesAvailable);
		
		lblNewUpdateURL = new Label(shell, SWT.WRAP);
		lblNewUpdateURL.setVisible(false);
		FormData fd_lblNewUpdateURL = new FormData();
		fd_lblNewUpdateURL.top = new FormAttachment(lblNewUpdatesAvailable);
		fd_lblNewUpdateURL.left = new FormAttachment(10);
		fd_lblNewUpdateURL.right = new FormAttachment(100);
		lblNewUpdateURL.setLayoutData(fd_lblNewUpdateURL);
		lblNewUpdateURL.setForeground(SWTResourceManager.getColor(SWT.COLOR_LINK_FOREGROUND));

		lblNewUpdateURL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				org.eclipse.swt.program.Program.launch(lblNewUpdateURL.getText());
			}
		});
		lblNewUpdateURL.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				Cursor cursor = shell.getCursor();
				if (cursor != null)
					cursor.dispose();
				cursor = new Cursor(shell.getDisplay(), SWT.CURSOR_HAND); 
				lblNewUpdateURL.setCursor(cursor);
			}
		});
		
		lblProgress = new Label(shell, SWT.NONE);
		FormData fd_lblProgress = new FormData();
		fd_lblProgress.right = new FormAttachment(100);
		lblProgress.setLayoutData(fd_lblProgress);
		lblProgress.setText("Ready...");
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		FormData fd_progressBar = new FormData();
		progressBar.setLayoutData(fd_progressBar);
		
		btnCancel = new Button(shell, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCancel();
			}
		});
		
		btnGo = new Button(shell, SWT.NONE);
		fd_btnCancel.right = new FormAttachment(btnGo, -6);
		FormData fd_btnSend = new FormData();
		fd_btnSend.bottom = new FormAttachment(100, -10);
		fd_btnSend.right = new FormAttachment(100, -10);
		btnGo.setLayoutData(fd_btnSend);
		btnGo.setText("Check for Updates");
		btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSend();
			}
		});
		
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(btnGo, -10);
		
		fd_progressBar.bottom = new FormAttachment(btnCancel, -10);
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		
		fd_lblProgress.bottom = new FormAttachment(progressBar, -6);
		fd_lblProgress.left = new FormAttachment(0, 10);
		
		lblProgress.setEnabled(false);
		progressBar.setEnabled(false);
		
		return shell;
	}
}
