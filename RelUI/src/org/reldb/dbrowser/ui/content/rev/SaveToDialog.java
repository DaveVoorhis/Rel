package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class SaveToDialog extends Dialog {
	private String prompt;
	private String name;

	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SaveToDialog(Shell parentShell, String prompt, String name) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		setBlockOnOpen(true);
		this.prompt = prompt;
		this.name = name;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(prompt);
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
		lblNewLabel.setText("Script name:");
		
		Button chkUseScratchpad = new Button(container, SWT.CHECK);
		chkUseScratchpad.setText("To command-line");
		FormData fd_chkKeepOriginal = new FormData();
		fd_chkKeepOriginal.top = new FormAttachment(lblNewLabel, 10);
		fd_chkKeepOriginal.right = new FormAttachment(100, -10);
		chkUseScratchpad.setLayoutData(fd_chkKeepOriginal);
		chkUseScratchpad.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				if (chkUseScratchpad.getSelection()) {
					text.setText("scratchpad");
					text.setEnabled(false);
				} else {
					text.setText(name);
					text.setEnabled(true);
				}
			}
		});
		
		text = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 10);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		fd_text.right = new FormAttachment(100, -10);
		fd_text.width = 300;
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
	
}
