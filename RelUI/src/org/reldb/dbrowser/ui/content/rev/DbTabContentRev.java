package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rev.core2.Rev;

public class DbTabContentRev extends Composite {
	
    private Rev rev;
    
	public DbTabContentRev(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

	    rev = new Rev(this, parentTab, parentTab.getCrashHandler());	    

		CmdPanelToolbar toolBar = new CmdPanelToolbar(this, rev.getCmdPanelOutput()) {
			public void addAdditionalItems(ToolBar toolbar) {
				// backup icon
				ToolItem tlitmBackup = new ToolItem(toolbar, SWT.NONE);
				tlitmBackup.setToolTipText("Make backup");
				tlitmBackup.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						parentTab.makeBackup();
					}
				});
				addAdditionalItem(tlitmBackup, "safeIcon");
			}
		};
		
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.getToolBar().setLayoutData(fd_toolBar);

		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar.getToolBar());
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		rev.setLayoutData(fd_composite);
	}

	public void redisplayed() {
		rev.refresh();
	}

}
