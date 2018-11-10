package org.reldb.dbrowser.ui.content.rel.var;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Connection.ExecuteResult;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;

public class VarExternalDefinitionDialog extends Dialog {

	private static String lastFilePath;

	private FileDialog loadFileDialog;
	private Shell shlExternalDefinitionDialog;
	private RevDatabase database;
	private String variableName;
	private String variableType;
	private Text textVarName;
	private boolean success;

	private Button btnAUTOKEY = null;
	private Button btnDUP_REMOVE = null;
	private Button btnDUP_COUNT = null;

	public VarExternalDefinitionDialog(Shell shell, int style) {
		super(shell, style);
	}

	public VarExternalDefinitionDialog(RevDatabase database, Shell shell, String variableType, String variableName) {
		super(shell, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.database = database;
		this.variableType = variableType;
		this.variableName = variableName;
		success = false;
	}

	private static class ComponentInfo {
		public boolean isOptional;
		public boolean isFile;
		public String content;
		public Label docs;

		public ComponentInfo(boolean isOptional, Label docs, boolean isFile) {
			this.isOptional = isOptional;
			this.docs = docs;
			this.content = "";
			this.isFile = isFile;
		}

		public ComponentInfo(ComponentInfo info, String content) {
			this.isOptional = info.isOptional;
			this.docs = info.docs;
			this.isFile = info.isFile;
			this.content = content;
		}
	}

	private HashMap<Integer, ComponentInfo> components = new HashMap<Integer, ComponentInfo>();

	private void createComponent(int componentNumber, boolean isOptional, Label docsLabel, boolean isFile) {
		components.put(componentNumber, new ComponentInfo(isOptional, docsLabel, isFile));
	}

	private void updateComponent(int componentNumber, String content) {
		ComponentInfo componentInfo = components.get(componentNumber);
		components.put(componentNumber, new ComponentInfo(componentInfo, content));
	}

	/**
	 * Open and process dialog. Return true if relvar created.
	 * 
	 * @deprecated Retained only to make WindowBuilder work. Use {@link #create()}
	 *             instead.
	 */
	@Deprecated
	public boolean open() {
		return create();
	}

	public boolean create() {
		createContents();
		shlExternalDefinitionDialog.open();
		shlExternalDefinitionDialog.layout();
		Display display = getParent().getDisplay();
		while (!shlExternalDefinitionDialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return success;
	}

	private void createContents() {
		shlExternalDefinitionDialog = new Shell(getParent(), getStyle());
		shlExternalDefinitionDialog.setSize(450, 318);
		shlExternalDefinitionDialog.setText("External Variable Definition");
		shlExternalDefinitionDialog.setLayout(new FormLayout());

		Tuples componentDefinitions = null;
		Tuple tuple = database.getExternalRelvarTypeInfo(variableType);
		if (tuple != null) {
			componentDefinitions = (Tuples) tuple.get("Components");
		}

		Composite container = new Composite(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		container.setLayoutData(fd_scrolledComposite);

		GridLayout containerLayout = new GridLayout();
		containerLayout.numColumns = 1;
		containerLayout.marginWidth = 0;
		containerLayout.marginHeight = 0;
		container.setLayout(containerLayout);

		if (componentDefinitions != null)
			for (Tuple component : componentDefinitions) {
				int componentNumber = component.get("ComponentNumber").toInt();
				boolean isOptional = component.get("isOptional").toBoolean();
				boolean isAFile = component.get("isAFile").toBoolean();
				/* FileExtensions REL {Extension CHAR} */
				Tuples extensions = (Tuples) component.get("FileExtensions");
				String docs = component.get("Documentation").toString();
				/* ComponentOptions REL {Documentation CHAR, OptionText CHAR} */
				Tuples options = (Tuples) component.get("ComponentOptions");

				Composite componentPanel = new Composite(container, SWT.NONE);
				componentPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				componentPanel.setLayout(new FormLayout());

				Label docsLabel = new Label(componentPanel, SWT.NONE);
				FormData fd_docsLabel = new FormData();
				fd_docsLabel.top = new FormAttachment(0);
				fd_docsLabel.left = new FormAttachment(0);
				fd_docsLabel.right = new FormAttachment(100);
				docsLabel.setLayoutData(fd_docsLabel);
				docsLabel.setText(docs + ((isOptional) ? " (optional)" : ""));

				createComponent(componentNumber, isOptional, docsLabel, isAFile);

				Vector<Tuple> optionTuples = new Vector<Tuple>();
				for (Tuple optionTuple : options)
					optionTuples.add(optionTuple);

				if (optionTuples.size() == 0) {
					// vectors of file extensions
					Vector<String> fileExtensions = new Vector<String>();
					Vector<String> fileExtensionDescriptions = new Vector<String>();
					// fill-in blank
					Button fileButton = null;
					if (isAFile) {
						fileButton = new Button(componentPanel, SWT.PUSH);
						fileButton.setText("...");
						FormData fd_fileButton = new FormData();
						fd_fileButton.top = new FormAttachment(docsLabel, 4);
						fd_fileButton.right = new FormAttachment(100);
						fileButton.setLayoutData(fd_fileButton);
						for (Tuple fileExtension : extensions) {
							String extension = fileExtension.get("Extension").toString();
							fileExtensions.add("*." + extension);
							fileExtensionDescriptions.add(extension);
						}
						fileExtensions.add("*.*");
						fileExtensionDescriptions.add("All Files");
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
					componentText.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							updateComponent(componentNumber, componentText.getText());
						}
					});
					// file button listener
					if (fileButton != null)
						fileButton.addListener(SWT.Selection, e -> {
							if (loadFileDialog == null) {
								loadFileDialog = new FileDialog(shlExternalDefinitionDialog, SWT.OPEN);
								if (lastFilePath == null)
									lastFilePath = System.getProperty("user.home");
								loadFileDialog.setFilterPath(lastFilePath);
								loadFileDialog.setText("Get File Path");
							}
							loadFileDialog.setFilterExtensions(fileExtensions.toArray(new String[0]));
							loadFileDialog.setFilterNames(fileExtensionDescriptions.toArray(new String[0]));
							String fname = loadFileDialog.open();
							if (fname == null)
								return;
							lastFilePath = loadFileDialog.getFilterPath();
							componentText.setText(fname);
							updateComponent(componentNumber, componentText.getText());
							Path p = Paths.get(fname);
							String justName = p.getFileName().toString();
							if (variableName.equals(textVarName.getText()))
								textVarName.setText(justName);
						});
				} else if (optionTuples.size() == 1) {
					// checkbox option
					Button checkBox = new Button(componentPanel, SWT.CHECK);
					Tuple optionTuple = optionTuples.elementAt(0);
					String optionDoc = optionTuple.get("Documentation").toString();
					String optionText = optionTuple.get("OptionText").toString();
					checkBox.setText(optionText + ": " + optionDoc);
					FormData fd_checkBox = new FormData();
					fd_checkBox.top = new FormAttachment(docsLabel, 4);
					fd_checkBox.left = new FormAttachment(0);
					checkBox.setLayoutData(fd_checkBox);
					checkBox.addListener(SWT.Selection,
							e -> updateComponent(componentNumber, ((checkBox.getSelection()) ? optionText : "")));
				} else {
					// combobox options
					Combo combo = new Combo(componentPanel, SWT.NONE);
					for (Tuple optionTuple : optionTuples) {
						String optionDoc = optionTuple.get("Documentation").toString();
						String optionText = optionTuple.get("OptionText").toString();
						combo.add(optionText + ": " + optionDoc);
					}
					FormData fd_combo = new FormData();
					fd_combo.top = new FormAttachment(docsLabel, 4);
					fd_combo.left = new FormAttachment(0);
					combo.setLayoutData(fd_combo);
					combo.addListener(SWT.Selection, e -> updateComponent(componentNumber, combo.getText()));
				}
			}

		Label lblVarName = new Label(shlExternalDefinitionDialog, SWT.NONE);
		FormData fd_lblVarName = new FormData();
		fd_lblVarName.top = new FormAttachment(container, 10);
		fd_lblVarName.left = new FormAttachment(0, 10);
		lblVarName.setLayoutData(fd_lblVarName);
		lblVarName.setText("Name:");

		textVarName = new Text(shlExternalDefinitionDialog, SWT.BORDER);
		FormData fd_textVarName = new FormData();
		fd_textVarName.top = new FormAttachment(container, 10);
		fd_textVarName.left = new FormAttachment(lblVarName, 6);
		fd_textVarName.right = new FormAttachment(100, -10);
		textVarName.setLayoutData(fd_textVarName);
		textVarName.setText(variableName);

		Group groupDup = new Group(shlExternalDefinitionDialog, SWT.NONE);
		groupDup.setLayout(new FillLayout(SWT.VERTICAL));
		FormData fd_groupDup = new FormData();
		fd_groupDup.top = new FormAttachment(textVarName, 10);
		fd_groupDup.left = new FormAttachment(0, 10);
		fd_groupDup.right = new FormAttachment(100, -10);
		groupDup.setLayoutData(fd_groupDup);

		boolean isGuaranteedUnique = tuple.get("GuaranteedUnique").toBoolean();
		if (isGuaranteedUnique)
			groupDup.setVisible(false);
		else {
			btnAUTOKEY = new Button(groupDup, SWT.RADIO);
			btnAUTOKEY.setText("AUTOKEY: Automatically generate a key.");
			btnAUTOKEY.setSelection(true);

			btnDUP_REMOVE = new Button(groupDup, SWT.RADIO);
			btnDUP_REMOVE.setText("DUP_REMOVE: Silently remove duplicate tuples.");

			btnDUP_COUNT = new Button(groupDup, SWT.RADIO);
			btnDUP_COUNT.setText("DUP_COUNT: Count duplicate tuples.");
		}

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

		fd_groupDup.bottom = new FormAttachment(btnOk, -10);

		shlExternalDefinitionDialog.pack();

		btnCancel.addListener(SWT.Selection, e -> {
			variableName = null;
			variableType = null;
			success = false;
			shlExternalDefinitionDialog.dispose();
		});

		btnOk.addListener(SWT.Selection, e -> {
			variableName = textVarName.getText();
			String definition = "";
			boolean missing = false;
			for (int componentNumber = 0; componentNumber < components.size(); componentNumber++) {
				ComponentInfo component = components.get(componentNumber);
				String content = component.content.trim();
				if (component.isOptional && content.length() == 0)
					continue;
				if (definition.length() > 0)
					definition += ",";
				if (content.length() > 0)
					definition += (component.isFile) ? content.replace('\\', '/') : content;
				else if (!component.isOptional) {
					component.docs.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
					missing = true;
				}
			}

			String dupHandling = "";
			if (!isGuaranteedUnique) {
				if (btnDUP_REMOVE.getSelection())
					dupHandling = "DUP_REMOVE";
				else if (btnDUP_COUNT.getSelection())
					dupHandling = "DUP_COUNT";
				else if (btnAUTOKEY.getSelection())
					dupHandling = "AUTOKEY";
			}

			definition = "VAR " + variableName + " EXTERNAL " + variableType + " \"" + definition + "\" " + dupHandling
					+ ";";
			if (missing) {
				MessageDialog.openError(shlExternalDefinitionDialog, "Missing Information",
						"Components shown in red must be filled in.");
				return;
			}
			if (database.relvarExists(variableName)) {
				MessageDialog.openInformation(shlExternalDefinitionDialog, "Note",
						"A variable named " + variableName + " already exists.");
				return;
			}
			ExecuteResult result = database.exec(definition);
			if (result.failed()) {
				MessageDialog.openError(shlExternalDefinitionDialog, "Error",
						"Unable to create variable " + variableName + ": " + result.getErrorMessage());
				return;
			}
			success = true;
			shlExternalDefinitionDialog.dispose();
		});

		container.setFocus();
	}
}
