package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;

public class RelvarDesignerComposite extends Composite {
	private RelvarDesigner relvarDesigner;
	private CmdPanel cmdPanel;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RelvarDesignerComposite(Composite parent, DbConnection connection, String relvarName) {
		super(parent, SWT.NONE);
		
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);

		Composite designerSurround = new Composite(sashForm, SWT.BORDER);
		designerSurround.setLayout(new FillLayout(SWT.HORIZONTAL));
		relvarDesigner = new RelvarDesigner(designerSurround, connection, relvarName) {
			protected void changedDefinition() {
				String relDefinition = relvarDesigner.getRelDefinition();
				cmdPanel.setInputText(relDefinition);
				cmdPanel.setEnabled(relDefinition.length() > 0);
			}
		};

		try {
			cmdPanel = new CmdPanel(connection, sashForm, CmdPanel.NO_INPUT_TOOLBAR) {
				public void notifyExecuteSuccess() {
					relvarDesigner.refresh();
				}
			};
			cmdPanel.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "Unable to open database connection:\n\n" + e.getMessage());
		}
		
		sashForm.setWeights(new int[] {8, 3});
	}

	public RelvarDesigner getRelvarDesigner() {
		return relvarDesigner;
	}
}
