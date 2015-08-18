package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.DbConnection;

public class AttributeDesignerDialog extends Dialog {

	private DbConnection connection;
	private String headingDefinition;
	private AttributeDesigner attributeDesigner;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AttributeDesignerDialog(Shell parentShell, DbConnection connection) {
		super(parentShell);
		setBlockOnOpen(true);
		this.connection = connection;
	}
	
	protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText("Heading Designer");
	}
	
	protected Point getInitialSize() {
		return new Point(400, 300);
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

		attributeDesigner = new AttributeDesigner(container, connection);
		attributeDesigner.setHeadingDefinition(getHeadingDefinition());
		
		container.pack();
		
		return container;
	}
	
	protected void cancelPressed() {
		super.cancelPressed();
	}
	
	protected void okPressed() {
		headingDefinition = attributeDesigner.getHeadingDefinition();
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

	public String getHeadingDefinition() {
		return headingDefinition;
	}

	public void setHeadingDefinition(String headingDefinition) {
		this.headingDefinition = headingDefinition;
	}
}
