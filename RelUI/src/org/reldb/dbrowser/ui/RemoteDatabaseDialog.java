package org.reldb.dbrowser.ui;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.reldb.rel.shared.Defaults;

public class RemoteDatabaseDialog extends Dialog {

	private final static int defaultPort = Defaults.getDefaultPort();

	public static class RemoteDatabaseDialogResponse {
		private String domain;
		private int port;

		public RemoteDatabaseDialogResponse(String domain, int port) {
			this.domain = domain;
			this.port = port;
		}

		public String getDomain() {
			return domain;
		}

		public int getPort() {
			return port;
		}

		public String toString() {
			return domain + ":" + port;
		}
	}

	protected RemoteDatabaseDialogResponse result;
	protected Shell shlOpenRemoteDatabase;
	private Text domain;
	private Text port;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public RemoteDatabaseDialog(Shell parent) {
		super(parent, SWT.None);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public RemoteDatabaseDialogResponse open() {
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
		port.setText(String.valueOf(defaultPort));

		Button btnCancel = new Button(shlOpenRemoteDatabase, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(domain, 0, SWT.RIGHT);
		fd_btnCancel.left = new FormAttachment(100, -79);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, e -> shlOpenRemoteDatabase.dispose());

		Button btnOk = new Button(shlOpenRemoteDatabase, SWT.NONE);
		btnOk.setSelection(true);
		FormData fd_btnOk = new FormData();
		fd_btnOk.left = new FormAttachment(btnCancel, -73, SWT.LEFT);
		fd_btnOk.top = new FormAttachment(btnCancel, 0, SWT.TOP);
		fd_btnOk.right = new FormAttachment(btnCancel, -6);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		btnOk.addListener(SWT.Selection, e -> {
			String errStr = "";
			if (domain.getText().trim().length() == 0) {
				errStr += ((errStr.length() > 0) ? "\n" : "") + "You must specify a domain name or IP address.";
			}
			int portValue = 0;
			if (port.getText().trim().length() == 0) {
				errStr += ((errStr.length() > 0) ? "\n" : "") + "You must specify a port between 0 and 65535.";
			} else {
				try {
					portValue = Integer.parseInt(port.getText());
					if (portValue < 0)
						errStr += ((errStr.length() > 0) ? "\n" : "")
								+ "The port must be an integer between 0 and 65535.";
				} catch (NumberFormatException nfe) {
					errStr += ((errStr.length() > 0) ? "\n" : "") + "The port you entered, '" + port.getText()
							+ "' should be an integer between 0 and 65535.";
				}
			}
			if (errStr.length() > 0) {
				MessageDialog.openError(shlOpenRemoteDatabase, "Error", errStr);
			} else {
				result = new RemoteDatabaseDialogResponse(domain.getText(), portValue);
				shlOpenRemoteDatabase.dispose();
			}
		});
		shlOpenRemoteDatabase.setDefaultButton(btnOk);
	}
}
