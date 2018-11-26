package org.reldb.dbrowser.ui.content.recent;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.reldb.dbrowser.ui.DbTab;

public class RecentPanel extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RecentPanel(Composite parent, DbTab dbTab, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 5;
		formLayout.marginHeight = 5;
		setLayout(formLayout);

		Label lblConvert = new Label(this, SWT.NONE);
		FormData fd_lblConvert = new FormData();
		fd_lblConvert.top = new FormAttachment(0);
		fd_lblConvert.left = new FormAttachment(0);
		fd_lblConvert.right = new FormAttachment(100);
		lblConvert.setLayoutData(fd_lblConvert);
		lblConvert.setText("Recently-used database options go here.");
	}
}
