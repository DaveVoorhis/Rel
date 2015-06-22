package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class DeleteConfirmDialog extends Dialog {

	private int tupleCount = 0;
	private boolean noAskAgain = false;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DeleteConfirmDialog(Shell parentShell, int tupleCount) {
		super(parentShell);
		this.tupleCount = tupleCount;
		setBlockOnOpen(true);
	}

	public boolean getAskDeleteConfirm() {
		return !noAskAgain;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Composite image = new Composite(container, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.bottom = new FormAttachment(0, 42);
		fd_composite.right = new FormAttachment(0, 42);
		image.setLayoutData(fd_composite);
		image.setBackgroundImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		
		Label lblPrompt = new Label(container, SWT.NONE);
		FormData fd_lblPrompt = new FormData();
		fd_lblPrompt.top = new FormAttachment(0, 10);
		fd_lblPrompt.left = new FormAttachment(image, 10);
		fd_lblPrompt.right = new FormAttachment(100, -10);
		lblPrompt.setLayoutData(fd_lblPrompt);
		lblPrompt.setText("Press OK to delete " + tupleCount + " tuple" + ((tupleCount > 1) ? "s" : "") + ".  Press Cancel to do nothing.");
		
		Button btnNoAsk = new Button(container, SWT.CHECK);
		FormData fd_btnNoAsk = new FormData();
		fd_btnNoAsk.top = new FormAttachment(lblPrompt, 10);
		fd_btnNoAsk.bottom = new FormAttachment(100, -10);
		fd_btnNoAsk.right = new FormAttachment(100, -10);
		btnNoAsk.setLayoutData(fd_btnNoAsk);
		btnNoAsk.setSelection(noAskAgain);
		btnNoAsk.setText("Don't ask me again.");
		btnNoAsk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				noAskAgain = btnNoAsk.getSelection();
			}
		});

		container.pack();
		
		return container;
	}

	protected void buttonPressed() {}
	
	protected void cancelPressed() {
		buttonPressed();
		super.cancelPressed();
	}
	
	protected void okPressed() {
		buttonPressed();
		super.okPressed();
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
	}
}
