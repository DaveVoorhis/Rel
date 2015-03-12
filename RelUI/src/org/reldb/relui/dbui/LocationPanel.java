package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.handlers.NewDatabase;
import org.reldb.relui.handlers.OpenLocalDatabase;
import org.reldb.relui.handlers.OpenRemoteDatabase;

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
		tltmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				(new NewDatabase()).execute();
			}
		});

		ToolItem tltmOpenLocal = new ToolItem(toolBar, SWT.NONE);
		tltmOpenLocal.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBLocalIcon.png"));
		tltmOpenLocal.setToolTipText("Open local database");
		tltmOpenLocal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				(new OpenLocalDatabase()).execute();
			}
		});
		
		ToolItem tltmOpenRemote = new ToolItem(toolBar, SWT.NONE);
		tltmOpenRemote.setImage(ResourceManager.getPluginImage("RelUI", "icons/OpenDBRemoteIcon.png"));
		tltmOpenRemote.setToolTipText("Open remote database");
		tltmOpenRemote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				(new OpenRemoteDatabase()).execute();
			}
		});
		
		textDatabase = new Text(this, SWT.BORDER);
		FormData fd_textDatabase = new FormData();
		fd_textDatabase.left = new FormAttachment(toolBar, 0);
		fd_textDatabase.right = new FormAttachment(100);
		fd_textDatabase.top = new FormAttachment(0);
		textDatabase.setLayoutData(fd_textDatabase);
		textDatabase.setToolTipText("Local or remote database URI");
	}
}
