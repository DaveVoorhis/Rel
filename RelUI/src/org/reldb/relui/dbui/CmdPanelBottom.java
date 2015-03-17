package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;

public class CmdPanelBottom extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanelBottom(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblRowCol = new Label(this, SWT.BORDER);
		FormData fd_lblRowCol = new FormData();
		fd_lblRowCol.left = new FormAttachment(0);
		lblRowCol.setLayoutData(fd_lblRowCol);
		lblRowCol.setText("0:0");
		
		ProgressBar progressBarBusy = new ProgressBar(this, SWT.NONE);
		fd_lblRowCol.top = new FormAttachment(progressBarBusy, 0, SWT.TOP);
		FormData fd_progressBarBusy = new FormData();
		fd_progressBarBusy.right = new FormAttachment(100);
		fd_progressBarBusy.left = new FormAttachment(100, -71);
		progressBarBusy.setLayoutData(fd_progressBarBusy);
		
		Button btnGo = new Button(this, SWT.BORDER);
		fd_progressBarBusy.top = new FormAttachment(btnGo, 0, SWT.TOP);
		FormData fd_btnGo = new FormData();
		fd_btnGo.right = new FormAttachment(progressBarBusy);
		fd_btnGo.left = new FormAttachment(lblRowCol);
		fd_btnGo.top = new FormAttachment(0);
		btnGo.setLayoutData(fd_btnGo);
		btnGo.setText("Run");
		btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				go();
			}
		});
	}
	
	public void go() {}
}
