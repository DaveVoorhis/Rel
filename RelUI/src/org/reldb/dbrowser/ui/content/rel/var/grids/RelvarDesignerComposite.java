package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.DbConnection;

public class RelvarDesignerComposite extends Composite {
	private RelvarDesigner relvarDesigner;
	private Text textActions;
	private Button btnApply;

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
				textActions.setText(relDefinition);
				btnApply.setEnabled(relDefinition.length() > 0);		
			}
		};
		
		Composite outputPanel = new Composite(sashForm, SWT.BORDER);
		outputPanel.setLayout(new FormLayout());
		
		btnApply = new Button(outputPanel, SWT.NONE);
		btnApply.setEnabled(false);
		FormData fd_btnApply = new FormData();
		fd_btnApply.left = new FormAttachment(0, 5);
		fd_btnApply.bottom = new FormAttachment(100, -5);
		btnApply.setLayoutData(fd_btnApply);
		btnApply.setText("Apply");
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		textActions = new Text(outputPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_textActions = new FormData();
		fd_textActions.top = new FormAttachment(0, 5);
		fd_textActions.left = new FormAttachment(0, 5);
		fd_textActions.bottom = new FormAttachment(btnApply, -5);
		fd_textActions.right = new FormAttachment(100, -5);
		textActions.setLayoutData(fd_textActions);
		
		sashForm.setWeights(new int[] {8, 3});
	}

	public RelvarDesigner getRelvarDesigner() {
		return relvarDesigner;
	}
}
