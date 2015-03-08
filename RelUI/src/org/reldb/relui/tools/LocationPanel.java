package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
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
		tltmNew.setImage(ResourceManager.getPluginImage("RelUI", "icons/NewDBIcon.png"));
		tltmNew.setToolTipText("New database");

		ToolItem tltmOpenLocal = new ToolItem(toolBar, SWT.NONE);
		tltmOpenLocal.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBLocalIcon.png"));
		tltmOpenLocal.setToolTipText("Open local database");
		
		ToolItem tltmOpenRemote = new ToolItem(toolBar, SWT.NONE);
		tltmOpenRemote.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBRemoteIcon.png"));
		tltmOpenRemote.setToolTipText("Open remote database");
		
		textDatabase = new Text(this, SWT.BORDER);
		FormData fd_textDatabase = new FormData();
		fd_textDatabase.left = new FormAttachment(toolBar, 6);
		fd_textDatabase.top = new FormAttachment(0);
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
