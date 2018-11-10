package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;

public class CmdPanelBottom extends Composite {

	private Label lblRowCol;
	private Button btnGo;
	private Button btnCancel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanelBottom(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		lblRowCol = new Label(this, SWT.BORDER);
		FormData fd_lblRowCol = new FormData();
		fd_lblRowCol.width = 80;
		fd_lblRowCol.left = new FormAttachment(0);
		fd_lblRowCol.top = new FormAttachment(0);
		fd_lblRowCol.bottom = new FormAttachment(100);
		lblRowCol.setLayoutData(fd_lblRowCol);
		lblRowCol.setText("0000:0000");
				
		btnGo = new Button(this, SWT.NONE);
		FormData fd_btnGo = new FormData();
		fd_btnGo.left = new FormAttachment(lblRowCol);
		fd_btnGo.top = new FormAttachment(0);
		btnGo.setLayoutData(fd_btnGo);
		btnGo.setText("Run");
		btnGo.addListener(SWT.Selection, e -> go());
		
		btnCancel = new Button(this, SWT.NONE);
		btnCancel.setEnabled(false);
		btnCancel.setLayoutData(new FormData());
		btnCancel.setText("Cancel");
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.width = 80;		
		fd_btnCancel.right = new FormAttachment(100);
		fd_btnCancel.top = new FormAttachment(0);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.addListener(SWT.Selection, e -> cancel());
		
		fd_btnGo.right = new FormAttachment(btnCancel);
	}
	
	public void go() {}
	
	public void cancel() {}
	
	public void setEnabledRunButton(boolean b) {
		if (!isDisposed())
			getDisplay().asyncExec(() -> {
				if (!isDisposed()) {
					btnGo.setEnabled(b);
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
