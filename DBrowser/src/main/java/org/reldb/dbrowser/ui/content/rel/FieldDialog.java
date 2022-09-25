package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

public class FieldDialog extends Dialog {
	private String text;
	private Text field;
	private String windowPrompt;
	private String fieldPrompt;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public FieldDialog(Shell parentShell, String windowPrompt, String fieldPrompt, String initialText) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		setBlockOnOpen(true);
		text = initialText;
		this.windowPrompt = windowPrompt;
		this.fieldPrompt = fieldPrompt;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(windowPrompt);
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
		lblNewLabel.setText(fieldPrompt);
		
		field = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 10);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		fd_text.right = new FormAttachment(100, -10);
		field.setLayoutData(fd_text);
		field.setText(text);
		field.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(field.getText().trim().length() > 0);
			}
		});
		
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
		text = field.getText();
		super.okPressed();
	}

	public String getText() {
		return text;
	}
}
