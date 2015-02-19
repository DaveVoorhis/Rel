package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class LocationPanel extends Composite {
	private Text textDatabase;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LocationPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 8);
		fd_lblNewLabel.left = new FormAttachment(0, 5);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Database:");
		
		textDatabase = new Text(this, SWT.BORDER);
		fd_lblNewLabel.right = new FormAttachment(textDatabase, -1);
		FormData fd_textDatabase = new FormData();
		fd_textDatabase.left = new FormAttachment(0, 64);
		fd_textDatabase.top = new FormAttachment(0, 5);
		textDatabase.setLayoutData(fd_textDatabase);
		
		Button btnChooser = new Button(this, SWT.NONE);
		fd_textDatabase.right = new FormAttachment(btnChooser, -6);
		FormData fd_btnChooser = new FormData();
		fd_btnChooser.right = new FormAttachment(100);
		fd_btnChooser.top = new FormAttachment(0, 1);
		btnChooser.setLayoutData(fd_btnChooser);
		btnChooser.setText("...");
	}
	
}
