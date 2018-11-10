package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ControlPanel extends Dialog {
	
	private final Visualiser visualiser;

	public ControlPanel(Visualiser visualiser) {
		super(visualiser.getShell());
		this.visualiser = visualiser;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		setBlockOnOpen(true);
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(visualiser.getTitle());
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		buildContents(container);
		return container;
	}

	protected void buildContents(Composite parent) {}
}
