package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.content.cmd.RelLineStyler;
import org.reldb.dbrowser.ui.content.rel.ExporterDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

public class ViewQueryDialog extends Dialog {

	private final static String copyToCommandPrompt = "Copy to Command-line";
	private final static String saveToViewPrompt = "Export View script";
	private final static String saveToOperatorPrompt = "Export Operator script";
	private final static String exportToFilePrompt = "Export query results to file";

	private Visualiser visualiser;

	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ViewQueryDialog(Visualiser visualiser) {
		super(visualiser.getShell(), 0);
		setText("View and Export Query");
		this.visualiser = visualiser;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private boolean allowOverwrite(String scriptName) {
		if (scriptName.equals("scratchpad"))
			return true;
		return MessageDialog.openConfirm(shell, "Overwrite?",
				"A script named '" + scriptName + "' already exists.  Overwrite it?");
	}

	private void copyToCmd(String source) {
		DbTab dbTab = visualiser.getModel().getRev().getDbTab();
		dbTab.setAndDisplayCmdContent(source);
	}

	private void doSave(String source, String scriptName) {
		if (scriptName.equals("scratchpad")) {
			copyToCmd(source);
		} else {
			visualiser.getModel().getRev().changeCatalog(RelPanel.CATEGORY_SCRIPT, scriptName);
			visualiser.getDatabase().setScript(scriptName, source);
		}
	}

	private void doSaveToView(String viewName, String scriptName) {
		String source = "VAR " + viewName.replace(' ', '_') + " VIEW " + visualiser.getQuery() + ';';
		doSave(source, scriptName);
	}

	private void doSaveToOperator(String operatorName, String scriptName) {
		String source = "OPERATOR " + operatorName.replace(' ', '_') + "() RETURNS SAME_TYPE_AS("
				+ visualiser.getQuery() + ");\n" + "\tRETURN " + visualiser.getQuery() + ";\n" + "END OPERATOR;";
		doSave(source, scriptName);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		shell.setSize(600, 300);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

		StyledText styledText = new StyledText(shell, SWT.BORDER);
		styledText.addLineStyleListener(new RelLineStyler(visualiser.getDatabase().getKeywords()));
		styledText.setBottomMargin(5);
		styledText.setTopMargin(5);
		styledText.setRightMargin(5);
		styledText.setLeftMargin(5);
		styledText.setEditable(false);
		styledText.setText(visualiser.getQuery());

		FormData fd_styledText = new FormData();
		fd_styledText.left = new FormAttachment(0);
		fd_styledText.top = new FormAttachment(0);
		fd_styledText.right = new FormAttachment(100, 0);
		styledText.setLayoutData(fd_styledText);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, 0);
		fd_composite.right = new FormAttachment(100, 0);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);

		fd_styledText.bottom = new FormAttachment(composite, 0);

		Button btnSaveToView = new Button(composite, SWT.NONE);
		btnSaveToView.setText(saveToViewPrompt);
		btnSaveToView.addListener(SWT.Selection, e -> {
			SaveToDialog saveToView = new SaveToDialog(shell, saveToViewPrompt, visualiser.getID());
			if (saveToView.open() == IDialogConstants.OK_ID) {
				String scriptName = saveToView.getName();
				if (!visualiser.getDatabase().scriptExists(scriptName) || allowOverwrite(scriptName)) {
					doSaveToView(visualiser.getID(), scriptName);
					close();
				}
			}
		});

		Button btnSaveToOperator = new Button(composite, SWT.NONE);
		btnSaveToOperator.setText(saveToOperatorPrompt);
		btnSaveToOperator.addListener(SWT.Selection, e -> {
			SaveToDialog saveToOperator = new SaveToDialog(shell, saveToOperatorPrompt, visualiser.getID());
			if (saveToOperator.open() == IDialogConstants.OK_ID) {
				String scriptName = saveToOperator.getName();
				if (!visualiser.getDatabase().scriptExists(scriptName) || allowOverwrite(scriptName)) {
					doSaveToOperator(visualiser.getID(), scriptName);
					close();
				}
			}
		});

		new Label(composite, SWT.NONE);

		Button btnExportToFile = new Button(composite, SWT.NONE);
		btnExportToFile.setText(exportToFilePrompt);
		btnExportToFile.addListener(SWT.Selection, e -> {
			ExporterDialog.runQueryToExport(shell, visualiser.getDatabase(),
					visualiser.getModel().getModelName() + "_" + visualiser.getTitle(), visualiser.getQuery());
		});

		Button btnCopyToCommandPrompt = new Button(composite, SWT.NONE);
		btnCopyToCommandPrompt.setText(copyToCommandPrompt);
		btnCopyToCommandPrompt.addListener(SWT.Selection, e -> {
			copyToCmd(visualiser.getQuery());
			close();
		});

		Button btnClose = new Button(composite, SWT.NONE);
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnClose.setText("Close");
		btnClose.addListener(SWT.Selection, e -> close());

		btnClose.setFocus();
	}

	public void close() {
		shell.dispose();
	}
}
