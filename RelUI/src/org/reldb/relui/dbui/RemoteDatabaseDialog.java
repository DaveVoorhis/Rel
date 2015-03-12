package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class RemoteDatabaseDialog extends Dialog {

	protected Object result;
	protected Shell shlOpenRemoteDatabase;
	private Text domain;
	private Text port;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RemoteDatabaseDialog(Shell parent) {
		super(parent, SWT.None);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlOpenRemoteDatabase.open();
		shlOpenRemoteDatabase.layout();
		Display display = getParent().getDisplay();
		while (!shlOpenRemoteDatabase.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlOpenRemoteDatabase = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shlOpenRemoteDatabase.setSize(450, 112);
		shlOpenRemoteDatabase.setText("Open Remote Database");
		shlOpenRemoteDatabase.setLayout(new FormLayout());
		
		Label lblDomain = new Label(shlOpenRemoteDatabase, SWT.NONE);
		FormData fd_lblDomain = new FormData();
		fd_lblDomain.top = new FormAttachment(0, 10);
		fd_lblDomain.left = new FormAttachment(0, 10);
		lblDomain.setLayoutData(fd_lblDomain);
		lblDomain.setText("Domain name or IP address:");
		
		Label lblPort = new Label(shlOpenRemoteDatabase, SWT.NONE);
		FormData fd_lblPort = new FormData();
		fd_lblPort.left = new FormAttachment(0, 10);
		lblPort.setLayoutData(fd_lblPort);
		lblPort.setText("Port:");
		
		domain = new Text(shlOpenRemoteDatabase, SWT.BORDER);
		FormData fd_domain = new FormData();
		fd_domain.right = new FormAttachment(100, -10);
		fd_domain.left = new FormAttachment(lblDomain, 6);
		fd_domain.top = new FormAttachment(0, 5);
		domain.setLayoutData(fd_domain);
		
		port = new Text(shlOpenRemoteDatabase, SWT.BORDER);
		fd_lblPort.top = new FormAttachment(port, 6, SWT.TOP);
		FormData fd_port = new FormData();
		fd_port.top = new FormAttachment(lblDomain, 6);
		fd_port.left = new FormAttachment(0, 48);
		port.setLayoutData(fd_port);
		
		Button btnCancel = new Button(shlOpenRemoteDatabase, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(domain, 0, SWT.RIGHT);
		fd_btnCancel.left = new FormAttachment(100, -79);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		Button btnOk = new Button(shlOpenRemoteDatabase, SWT.NONE);
		btnOk.setSelection(true);
		FormData fd_btnOk = new FormData();
		fd_btnOk.left = new FormAttachment(btnCancel, -73, SWT.LEFT);
		fd_btnOk.top = new FormAttachment(btnCancel, 0, SWT.TOP);
		fd_btnOk.right = new FormAttachment(btnCancel, -6);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
	}
}
