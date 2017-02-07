package org.reldb.dbrowser.ui.content.rel.var;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class VarExternalDefinitionDialog extends Dialog {
	
	private Shell shlExternalDefinitionDialog;
	private RevDatabase database;
	private String variableName;
	private String variableType;
	private Text textVarName;
	private Text textDocumentation;

	public VarExternalDefinitionDialog(Shell shell, int style) {
		super(shell, style);
	}

	public VarExternalDefinitionDialog(RevDatabase database, Shell shell, String variableType, String variableName) {
		super(shell, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.database = database;
		this.variableType = variableType;
		this.variableName = variableName;
	}

	public String getName() {
		return variableName;
	}

	public String open() {
		createContents();
		shlExternalDefinitionDialog.open();
		shlExternalDefinitionDialog.layout();
		Display display = getParent().getDisplay();
		while (!shlExternalDefinitionDialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	private void createContents() {
		shlExternalDefinitionDialog = new Shell(getParent(), getStyle());
		shlExternalDefinitionDialog.setSize(450, 318);
		shlExternalDefinitionDialog.setText("External Variable Definition");
		shlExternalDefinitionDialog.setLayout(new FormLayout());
		
		String documentation = null;
		Tuples components = null;
		Tuple tuple = database.getExternalRelvarTypeInfo(variableType);
		if (tuple != null) {
			documentation = tuple.get("Documentation").toString();
			components = (Tuples)tuple.get("Components");
		}
		
		Label lblVarName = new Label(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_lblVarName = new FormData();
		fd_lblVarName.top = new FormAttachment(0, 10);
		fd_lblVarName.left = new FormAttachment(0, 10);
		lblVarName.setLayoutData(fd_lblVarName);
		lblVarName.setText("Name:");
		
		textVarName = new Text(shlExternalDefinitionDialog, SWT.BORDER);
		FormData fd_textVarName = new FormData();
		fd_textVarName.right = new FormAttachment(100, -10);
		fd_textVarName.top = new FormAttachment(0, 10);
		fd_textVarName.left = new FormAttachment(lblVarName, 6);
		textVarName.setLayoutData(fd_textVarName);
		textVarName.setText(variableName);
		
		textDocumentation = new Text(shlExternalDefinitionDialog, SWT.NONE);
		textDocumentation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		textDocumentation.setEditable(false);
		FormData fd_textDocumentation = new FormData();
		fd_textDocumentation.right = new FormAttachment(100, -10);
		fd_textDocumentation.top = new FormAttachment(textVarName, 10);
		fd_textDocumentation.left = new FormAttachment(0, 10);
		textDocumentation.setLayoutData(fd_textDocumentation);
		textDocumentation.setText((documentation != null) ? documentation : "");
		
		Button btnCancel = new Button(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
				
		Button btnOk = new Button(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_btnOk = new FormData();
		fd_btnOk.bottom = new FormAttachment(100, -10);
		fd_btnOk.right = new FormAttachment(btnCancel, -10);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("Ok");
		
		Composite container = new Composite(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		fd_scrolledComposite.bottom = new FormAttachment(btnOk, -10);
		container.setLayoutData(fd_scrolledComposite);
		
		fd_textDocumentation.bottom = new FormAttachment(container, -10);
		
		GridLayout containerLayout = new GridLayout();
		containerLayout.numColumns = 1;
		containerLayout.marginWidth = 0;
		containerLayout.marginHeight = 0;
		container.setLayout(containerLayout);
		
		if (components != null)
			for (Tuple component: components) {
				boolean isOptional = component.get("isOptional").toBoolean();
				boolean isAFile = component.get("isAFile").toBoolean();
				/* FileExtensions REL {Extension CHAR} */
				Tuples extensions = (Tuples)component.get("FileExtensions");
				String docs = component.get("Documentation").toString();
				/* ComponentOptions REL {Documentation CHAR, OptionText CHAR} */
				Tuples options = (Tuples)component.get("ComponentOptions");
				
				Composite componentPanel = new Composite(container, SWT.NONE);
				componentPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				componentPanel.setLayout(new FormLayout());
				
				Label docsLabel = new Label(componentPanel, SWT.NONE);
				FormData fd_docsLabel = new FormData();
				fd_docsLabel.top = new FormAttachment(0);
				fd_docsLabel.left = new FormAttachment(0);
				fd_docsLabel.right = new FormAttachment(100);
				docsLabel.setLayoutData(fd_docsLabel);
				docsLabel.setText(docs);

				Vector<Tuple> optionTuples = new Vector<Tuple>();
				for (Tuple optionTuple: options)
					optionTuples.add(optionTuple);
				
				if (optionTuples.size() == 0) {
					// fill-in blank
					Button fileButton = null;
					if (isAFile) {
						fileButton = new Button(componentPanel, SWT.PUSH);
						fileButton.setText("...");
						FormData fd_fileButton = new FormData();
						fd_fileButton.top = new FormAttachment(docsLabel, 4);
						fd_fileButton.right = new FormAttachment(100);
						fileButton.setLayoutData(fd_fileButton);
						// vector of file extensions
						Vector<String> fileExtensions = new Vector<String>();
						for (Tuple fileExtension: extensions)
							fileExtensions.add(fileExtension.get("Extension").toString());
					}
					Text componentText = new Text(componentPanel, SWT.BORDER);
					FormData fd_componentText = new FormData();
					fd_componentText.top = new FormAttachment(docsLabel, 4);
					fd_componentText.left = new FormAttachment(0);
					if (fileButton != null)
						fd_componentText.right = new FormAttachment(fileButton, -2);
					else
						fd_componentText.right = new FormAttachment(100);
					componentText.setLayoutData(fd_componentText);
				} else if (optionTuples.size() == 1) {
					// checkbox option
					Button checkBox = new Button(componentPanel, SWT.CHECK);
					Tuple optionTuple = optionTuples.elementAt(0);
					checkBox.setText(optionTuple.get("Documentation").toString() + " " + optionTuple.get("OptionText").toString());
					FormData fd_checkBox = new FormData();
					fd_checkBox.top = new FormAttachment(docsLabel, 4);
					fd_checkBox.left = new FormAttachment(0);
					checkBox.setLayoutData(fd_checkBox);
				} else {
					// combobox options
					Combo combo = new Combo(componentPanel, SWT.NONE);
					for (Tuple optionTuple: optionTuples) {
						String optionDoc = optionTuple.get("Documentation").toString();
						String optionText = optionTuple.get("OptionText").toString();
						combo.add(optionText + ": " + optionDoc);
					}
					FormData fd_combo = new FormData();
					fd_combo.top = new FormAttachment(docsLabel, 4);
					fd_combo.left = new FormAttachment(0);
					combo.setLayoutData(fd_combo);
				}
			}
		
		shlExternalDefinitionDialog.pack();
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableName = null;
				variableType = null;
				shlExternalDefinitionDialog.dispose();
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variableName = textVarName.getText();
				shlExternalDefinitionDialog.dispose();
			}
		});
	}
}
