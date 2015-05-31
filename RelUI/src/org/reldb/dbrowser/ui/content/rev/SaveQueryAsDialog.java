package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class SaveQueryAsDialog extends Dialog {
	private String name;
	private boolean keepOriginal;
	
	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SaveQueryAsDialog(Shell parentShell, String name) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		setBlockOnOpen(true);
		this.name = name;
		keepOriginal = false;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Save Query As");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 15);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Query name:");
		
		Button chkKeepOriginal = new Button(container, SWT.CHECK);
		chkKeepOriginal.setText("Retain original query.");
		FormData fd_chkKeepOriginal = new FormData();
		fd_chkKeepOriginal.top = new FormAttachment(lblNewLabel, 10);
		fd_chkKeepOriginal.right = new FormAttachment(100);
		chkKeepOriginal.setLayoutData(fd_chkKeepOriginal);
		chkKeepOriginal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				keepOriginal = chkKeepOriginal.getSelection();
			}
		});
		
		text = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 10);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		fd_text.right = new FormAttachment(100, -10);
		text.setLayoutData(fd_text);
		text.setText(name);
		
		container.pack();
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected void okPressed() {
		name = text.getText();
		super.okPressed();
	}

	public String getName() {
		return name;
	}
	
	public boolean keepOriginal() {
		return keepOriginal;
	}
}
