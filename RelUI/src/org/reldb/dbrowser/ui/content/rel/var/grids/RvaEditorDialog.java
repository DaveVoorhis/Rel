package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.DbConnection;

public class RvaEditorDialog extends Dialog {

	private DbConnection connection;
	private String rvaValue;
	private RvaEditor attributeEditor;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RvaEditorDialog(Shell parentShell, DbConnection connection) {
		super(parentShell);
		setBlockOnOpen(true);
		this.connection = connection;
	}
	
	protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText("Attribute Editor");
	}
	
	protected Point getInitialSize() {
		return new Point(600, 400);
	}
	
	protected boolean isResizable() {
	    return true;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite)super.createDialogArea(parent);
		container.setLayout(new FillLayout());

		attributeEditor = new RvaEditor(container, connection);
		attributeEditor.setRVAValue(getRVAValue());
		
		container.pack();
		
		return container;
	}
	
	protected void cancelPressed() {
		super.cancelPressed();
	}
	
	protected void okPressed() {
		attributeEditor.processDirtyRows();
		if (attributeEditor.countDirtyRows() > 0)
			return;
		rvaValue = attributeEditor.getRVAValue();
		super.okPressed();
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

	public String getRVAValue() {
		return rvaValue;
	}

	public void setRVAValue(String rvaValue) {
		this.rvaValue = rvaValue;
	}
}
