package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

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
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		
		ToolItem tltmNew = new ToolItem(toolBar, SWT.NONE);
		tltmNew.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-6-icon-16.png"));
		tltmNew.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-6-icon-16.png"));
		tltmNew.setImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-6-icon-16.png"));
		tltmNew.setToolTipText("New database");

		ToolItem tltmOpenLocal = new ToolItem(toolBar, SWT.NONE);
		tltmOpenLocal.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-icon-16.png"));
		tltmOpenLocal.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-icon-16.png"));
		tltmOpenLocal.setImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-icon-16.png"));
		tltmOpenLocal.setToolTipText("Open local database");
		
		ToolItem tltmOpenRemote = new ToolItem(toolBar, SWT.NONE);
		tltmOpenRemote.setDisabledImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-3-icon-16.png"));
		tltmOpenRemote.setHotImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-3-icon-16.png"));
		tltmOpenRemote.setImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-3-icon-16.png"));
		tltmOpenRemote.setToolTipText("Open remote database");
		
		Label lblDatabase = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0);
		fd_lblNewLabel.left = new FormAttachment(toolBar);
		lblDatabase.setLayoutData(fd_lblNewLabel);
		lblDatabase.setImage(ResourceManager.getPluginImage("RelUI", "icons/iconmonstr-database-2-icon-16_2.png"));
		lblDatabase.setToolTipText("Local or remote database URI");
		
		textDatabase = new Text(this, SWT.BORDER);
		FormData fd_textDatabase = new FormData();
		fd_textDatabase.top = new FormAttachment(0);
		fd_textDatabase.left = new FormAttachment(lblDatabase);
		textDatabase.setLayoutData(fd_textDatabase);
		textDatabase.setToolTipText("Local or remote database URI");
		
		Button btnChooser = new Button(this, SWT.NONE);
		fd_textDatabase.right = new FormAttachment(btnChooser);
		FormData fd_btnChooser = new FormData();
		fd_btnChooser.top = new FormAttachment(0);
		fd_btnChooser.right = new FormAttachment(100);
		btnChooser.setLayoutData(fd_btnChooser);
		btnChooser.setText("...");
		btnChooser.setToolTipText("Choose local or remote database.");
	}
}
