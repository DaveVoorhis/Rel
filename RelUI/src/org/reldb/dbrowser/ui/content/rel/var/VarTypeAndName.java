package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.RevDatabase;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class VarTypeAndName extends Dialog {

	private Shell shlVariableTypeAndName;
	private RevDatabase database;
	private String variableType;
	private String variableName;
	
	public VarTypeAndName(Shell shell, int style) {
		super(shell, style);
	}
	
	public VarTypeAndName(RevDatabase database, Shell shell, String variableName) {
		super(shell, SWT.DIALOG_TRIM);
		this.database = database;
		this.variableName = variableName;
		variableType = null;
	}

	private static String getVarTypeCode(String varType) {
		if (varType == null)
			return null;
		int colonPos = varType.indexOf(':');
		if (colonPos >= 0)
			return varType.substring(0, colonPos);
		return varType;
	}
	
	private static String getSelectedType(List listVarType) {
		String[] selected = listVarType.getSelection();
		if (selected == null || selected.length == 0)
			return null;
		return selected[0];
	}
	
	/**
	 * Open the dialog.
	 * @return the desired variable type
	 */
	public String open() {
		createContents();
		shlVariableTypeAndName.open();
		shlVariableTypeAndName.layout();
		Display display = getParent().getDisplay();
		while (!shlVariableTypeAndName.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return getVarTypeCode(variableType);
	}

	public String getVariableName() {
		return variableName;
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlVariableTypeAndName = new Shell(getParent(), getStyle());
		shlVariableTypeAndName.setSize(450, 318);
		shlVariableTypeAndName.setText("Variable Type and Name");
		shlVariableTypeAndName.setLayout(new FormLayout());
		
		Label lblChooseTheKind = new Label(shlVariableTypeAndName, SWT.NONE);
		FormData fd_lblChooseTheKind = new FormData();
		fd_lblChooseTheKind.left = new FormAttachment(0, 10);
		fd_lblChooseTheKind.top = new FormAttachment(0, 10);
		fd_lblChooseTheKind.bottom = new FormAttachment(0, 24);
		fd_lblChooseTheKind.right = new FormAttachment(100, -10);
		lblChooseTheKind.setLayoutData(fd_lblChooseTheKind);
		lblChooseTheKind.setText("Choose the kind of variable you wish to create.");
		
		List listVarType = new List(shlVariableTypeAndName, SWT.BORDER);
		FormData fd_listVarType = new FormData();
		fd_listVarType.top = new FormAttachment(lblChooseTheKind, 6);
		fd_listVarType.left = new FormAttachment(0, 10);
		fd_listVarType.right = new FormAttachment(100, -10);
		listVarType.setLayoutData(fd_listVarType);
		
		int index = 0;
		for (String relvarType: database.getRelvarTypes()) {
			listVarType.add(relvarType);
			if (getVarTypeCode(relvarType).equalsIgnoreCase("REAL"))
				listVarType.setSelection(index);
			index++;
		}
		
		Button btnCancel = new Button(shlVariableTypeAndName, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
				
		Button btnOk = new Button(shlVariableTypeAndName, SWT.NONE);
		FormData fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(100, -10);
		fd_btnOk.right = new FormAttachment(btnCancel, -10);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		
		Text textVarName = new Text(shlVariableTypeAndName, SWT.BORDER);
		FormData fd_textVarName = new FormData();
		fd_textVarName.bottom = new FormAttachment(btnOk, -10);
		fd_textVarName.left = new FormAttachment(0, 10);
		fd_textVarName.right = new FormAttachment(100, -10);
		textVarName.setLayoutData(fd_textVarName);
		textVarName.setText(variableName);
		
		Label lblVariableName = new Label(shlVariableTypeAndName, SWT.NONE);
		FormData fd_lblVariableName = new FormData();
		fd_lblVariableName.bottom = new FormAttachment(textVarName, -6);
		fd_lblVariableName.left = new FormAttachment(0, 10);
		fd_lblVariableName.right = new FormAttachment(100, -10);
		lblVariableName.setLayoutData(fd_lblVariableName);
		lblVariableName.setText("Variable name:");
		
		fd_listVarType.bottom = new FormAttachment(lblVariableName, -10);
		
		listVarType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableType = getSelectedType(listVarType);
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableName = null;
				variableType = null;
				shlVariableTypeAndName.dispose();
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableName = textVarName.getText();
				variableType = getSelectedType(listVarType);
				shlVariableTypeAndName.dispose();
			}
		});
	}
}
