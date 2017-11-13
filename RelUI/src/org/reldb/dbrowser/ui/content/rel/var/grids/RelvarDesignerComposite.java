package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelInput.ErrorInformation;

public class RelvarDesignerComposite extends Composite {
	private RelvarDesigner relvarDesigner;
	private CmdPanel cmdPanel;

	private Button applyButton;

	private boolean hasPendingChanges;

	private String tabIdentifier;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RelvarDesignerComposite(Composite parent, DbConnection connection, String relvarName, String tabIdentifier) {
		super(parent, SWT.NONE);

		this.tabIdentifier = tabIdentifier;

		hasPendingChanges = false;

		setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(this, SWT.NONE);

		Composite designer = new Composite(sashForm, SWT.BORDER);
		designer.setLayout(new FormLayout());

		relvarDesigner = new RelvarDesigner(designer, connection, relvarName) {
			protected void changedDefinition() {
				String relDefinition = relvarDesigner.getRelDefinition();
				cmdPanel.setInputText(relDefinition);
				hasPendingChanges = relDefinition.trim().length() > 0;
				cmdPanel.setEnabled(hasPendingChanges);
				applyButton.setEnabled(hasPendingChanges);
			}
		};
		FormData fd_designer = new FormData();
		fd_designer.left = new FormAttachment(0, 0);
		fd_designer.right = new FormAttachment(100, 0);
		fd_designer.top = new FormAttachment(0, 0);
		relvarDesigner.getControl().setLayoutData(fd_designer);

		Button showDetailsButton = new Button(designer, SWT.PUSH);
		showDetailsButton.setText("\u2190");
		FormData fd_showDetailsButton = new FormData();
		fd_showDetailsButton.right = new FormAttachment(100, 0);
		fd_showDetailsButton.bottom = new FormAttachment(100, 0);
		showDetailsButton.setLayoutData(fd_showDetailsButton);
		showDetailsButton.addListener(SWT.Selection, e -> {
			if (sashForm.getMaximizedControl() == null) {
				showDetailsButton.setText("\u2190");
				sashForm.setMaximizedControl(designer);
			} else {
				showDetailsButton.setText("\u2192");
				sashForm.setMaximizedControl(null);
			}
		});

		applyButton = new Button(designer, SWT.PUSH);
		applyButton.setText("Apply Changes");
		applyButton.setEnabled(false);
		FormData fd_applyButton = new FormData();
		fd_applyButton.left = new FormAttachment(0, 0);
		fd_applyButton.right = new FormAttachment(showDetailsButton, -10);
		fd_applyButton.bottom = new FormAttachment(100, 0);
		applyButton.setLayoutData(fd_applyButton);
		applyButton.addListener(SWT.Selection, e -> {
			if (cmdPanel != null)
				cmdPanel.run();
		});

		fd_designer.bottom = new FormAttachment(applyButton, -10);

		try {
			cmdPanel = new CmdPanel(connection, sashForm, CmdPanel.NO_INPUT_TOOLBAR) {
				public void notifyExecuteSuccess() {
					applyButton.setEnabled(false);
					hasPendingChanges = false;
					relvarDesigner.refresh();
				}

				protected void notifyError(ErrorInformation eInfo) {
					MessageDialog.openError(getShell(), "Error", "Unable to apply changes:\n\n" + eInfo.getMessage());
				}
			};
			cmdPanel.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Unable to open database connection:\n\n" + e.getMessage());
		}

		sashForm.setWeights(new int[] { 8, 3 });
		sashForm.setMaximizedControl(designer);
	}

	public RelvarDesigner getRelvarDesigner() {
		return relvarDesigner;
	}

	public boolean hasPendingChanges() {
		if (hasPendingChanges)
			if (MessageDialog.openQuestion(getShell(), "Discard Pending Changes?",
					"There are unapplied changes in the '" + tabIdentifier + "' tab. Discard them?"))
				return false;
		return hasPendingChanges;
	}
}
