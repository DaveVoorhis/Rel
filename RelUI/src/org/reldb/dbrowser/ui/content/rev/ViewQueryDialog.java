package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.content.cmd.RelLineStyler;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

public class ViewQueryDialog extends Dialog {
	
	private final static String saveToViewPrompt = "Export View script";
	private final static String saveToOperatorPrompt = "Export Operator script";

	private final static int BTN_SaveAsView = IDialogConstants.CLIENT_ID + 0;
	private final static int BTN_SaveAsOperator = IDialogConstants.CLIENT_ID + 1;

	private Visualiser visualiser;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ViewQueryDialog(Visualiser visualiser) {
		super(visualiser.getShell());
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.visualiser = visualiser;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("View and Export Query");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		StyledText styledText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		styledText.addLineStyleListener(new RelLineStyler());
		styledText.setBottomMargin(5);
		styledText.setTopMargin(5);
		styledText.setRightMargin(5);
		styledText.setLeftMargin(5);
		styledText.setText(visualiser.getQuery());

		return container;
	}
	
	@Override
	protected void buttonPressed(int buttonid) {
		switch (buttonid) {
		case BTN_SaveAsView:
			SaveToDialog saveToView = new SaveToDialog(getShell(), saveToViewPrompt, visualiser.getID());
			if (saveToView.open() == IDialogConstants.OK_ID)
				close();
			break;
		case BTN_SaveAsOperator: 
			SaveToDialog saveToOperator = new SaveToDialog(getShell(), saveToOperatorPrompt, visualiser.getID());
			if (saveToOperator.open() == IDialogConstants.OK_ID)
				close();
			break;
		default:
			super.buttonPressed(buttonid);
		}
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BTN_SaveAsView, saveToViewPrompt, false);
		createButton(parent, BTN_SaveAsOperator, saveToOperatorPrompt, false);
		createButton(parent, IDialogConstants.OK_ID, "Close", true);
	}

}
