package org.reldb.dbrowser.ui.content.rev;

import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

public class LoadQueryDialog extends Dialog {

	private String item;
	private Vector<String> items;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public LoadQueryDialog(Shell parentShell, Vector<String> items) {
		super(parentShell);
		this.items = items;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Load Query");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		List list = new List(container, SWT.BORDER);
		for (String item: items)
			list.add(item);
		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				item = list.getSelection()[0];
			}
		});

		return container;
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

	public String getSelectedItem() {
		return item;
	}
	
}
