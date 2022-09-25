package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;

public abstract class CmdPanelBottom extends Composite {

	private Label lblRowCol;
	private Button btnGo;
	private Button btnEvaluate;
	private Button btnExecute;
	private Button btnCancel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanelBottom(Composite parent, int cmdStyle, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		lblRowCol = new Label(this, SWT.BORDER);
		FormData fd_lblRowCol = new FormData();
		lblRowCol.setLayoutData(fd_lblRowCol);
		lblRowCol.setText("0000:0000");
				
		btnGo = new Button(this, SWT.NONE);
		FormData fd_btnGo = new FormData();
		btnGo.setLayoutData(fd_btnGo);
		btnGo.setText("Run");
		btnGo.addListener(SWT.Selection, e -> go());

		FormData fd_btnEvaluate = null;
		FormData fd_btnExecute = null;
		if ((cmdStyle & CmdPanel.NO_EXECUTE_EVALUATE) == 0) {
			btnEvaluate = new Button(this, SWT.NONE);
			fd_btnEvaluate = new FormData();
			btnEvaluate.setLayoutData(fd_btnEvaluate);
			btnEvaluate.setText("Evaluate");
			btnEvaluate.addListener(SWT.Selection, e -> evaluate());
			
			btnExecute = new Button(this, SWT.NONE);
			fd_btnExecute = new FormData();
			btnExecute.setLayoutData(fd_btnExecute);
			btnExecute.setText("Execute");
			btnExecute.addListener(SWT.Selection, e -> execute());
		}
		
		btnCancel = new Button(this, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, e -> cancel());
		btnCancel.setEnabled(false);

		fd_lblRowCol.width = 80;
		fd_lblRowCol.left = new FormAttachment(0);
		fd_lblRowCol.top = new FormAttachment(0);
		fd_lblRowCol.bottom = new FormAttachment(100);
		
		fd_btnGo.left = new FormAttachment(lblRowCol);
		fd_btnGo.top = new FormAttachment(0);
		
		if (fd_btnEvaluate != null && fd_btnExecute != null) {	
			fd_btnGo.right = new FormAttachment(btnEvaluate);
			fd_btnEvaluate.width = 80;
			fd_btnEvaluate.top = new FormAttachment(0);
			fd_btnEvaluate.right = new FormAttachment(btnExecute);
			
			fd_btnExecute.width = 80;
			fd_btnExecute.top = new FormAttachment(0);
			fd_btnExecute.right = new FormAttachment(btnCancel);
		} else
			fd_btnGo.right = new FormAttachment(btnCancel);
		
		fd_btnCancel.width = 80;		
		fd_btnCancel.top = new FormAttachment(0);
		fd_btnCancel.right = new FormAttachment(100);
	}
	
	public abstract void go();
	
	public abstract void execute();
	
	public abstract void evaluate();
	
	public abstract void cancel();
	
	public void setEnabledRunButton(boolean b) {
		if (!isDisposed())
			getDisplay().asyncExec(() -> {
				if (!isDisposed()) {
					btnGo.setEnabled(b);
					if (btnEvaluate != null)
						btnEvaluate.setEnabled(b);
					if (btnExecute != null)
						btnExecute.setEnabled(b);
					btnCancel.setEnabled(!b);
				}
			});
	}
	
	public void setRunButtonPrompt(String s) {
		btnGo.setText(s);
	}
	
	public void setRowColDisplay(String s) {
		lblRowCol.setText(s);
	}
}
