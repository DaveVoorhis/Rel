package org.reldb.dbrowser.ui;

import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.DBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;

public class ManageRecentlyUsedDialog extends Dialog {

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ManageRecentlyUsedDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Recently Opened Databases");
	}

	private String[] recentlyOpenedList;
	private boolean[] removals;

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(container,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);

		recentlyOpenedList = DBrowser.getRecentlyUsedDatabaseList();
		removals = new boolean[recentlyOpenedList.length];

		Color backgroundHighlight = SWTResourceManager.getColor(220, 220, 220);

		int index = 0;
		for (String recentlyOpened : recentlyOpenedList) {
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			label.setText(recentlyOpened);
			label.setBackground(backgroundHighlight);

			Button button = new Button(composite, SWT.PUSH);
			button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			button.setText("Click to Remove");
			final int localIndex = index;
			button.addListener(SWT.Selection, e -> {
				removals[localIndex] = !removals[localIndex];
				button.setText((removals[localIndex]) ? "Removing" : "Click to Remove");
				label.setForeground((removals[localIndex]) ? SWTResourceManager.getColor(SWT.COLOR_GRAY)
						: SWTResourceManager.getColor(SWT.COLOR_BLACK));
				label.setBackground((removals[localIndex]) ? composite.getBackground() : backgroundHighlight);
			});

			index++;
		}

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected void okPressed() {
		setReturnCode(OK);
		Vector<String> keepers = new Vector<String>();
		int n = 0;
		for (String dbURL : recentlyOpenedList)
			if (!removals[n++])
				keepers.add(dbURL);
		DBrowser.setRecentlyUsedDatabaseList(keepers.toArray(new String[0]));
		close();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
