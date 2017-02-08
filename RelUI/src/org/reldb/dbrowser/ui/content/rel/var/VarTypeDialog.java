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
import org.reldb.dbrowser.ui.RevDatabase;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class VarTypeDialog extends Dialog {

	static private String lastVariableType;
	
	private Shell shlVariableTypeAndName;
	private RevDatabase database;
	private String variableType;
	
	public VarTypeDialog(Shell shell, int style) {
		super(shell, SWT.DIALOG_TRIM | SWT.RESIZE);
	}
	
	public VarTypeDialog(RevDatabase database, Shell shell) {
		super(shell, SWT.DIALOG_TRIM);
		this.database = database;
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
	
	private static String obtainSelectedType(List listVarType) {
		String[] selected = listVarType.getSelection();
		if (selected == null || selected.length == 0)
			return null;
		lastVariableType = getVarTypeCode(selected[0]);
		return lastVariableType;
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
		
		if (lastVariableType == null)
			lastVariableType = "REAL";
		int index = 0;
		for (String relvarType: database.getRelvarTypes()) {
			listVarType.add(relvarType);
			if (getVarTypeCode(relvarType).equalsIgnoreCase(lastVariableType))
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
		
		fd_listVarType.bottom = new FormAttachment(btnCancel, -10);
		
		listVarType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableType = obtainSelectedType(listVarType);
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableType = null;
				shlVariableTypeAndName.dispose();
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableType = obtainSelectedType(listVarType);
				shlVariableTypeAndName.dispose();
			}
		});
		
		listVarType.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				variableType = obtainSelectedType(listVarType);
				shlVariableTypeAndName.dispose();
			}
		});

	}
}
