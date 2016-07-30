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
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.var.RelvarEditorToolbar;

public class DbTabContentRev extends Composite {
	
    private Rev rev;
    private ManagedToolbar toolBar = null;
    
	private void addZoom(ToolBar toolbar) {
		new ToolItem(toolbar, SWT.SEPARATOR_FILL);
		// zoom
		ToolItem maximize = new ToolItem(toolbar, SWT.NONE);
		maximize.setImage(IconLoader.loadIcon("view_fullscreen"));
		maximize.setToolTipText("Zoom in or out");
		maximize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				zoom();
			}
		});
	}
    
    private void makeToolbar(DbTab parentTab) {
    	if (toolBar != null) {
    		toolBar.dispose();
    		toolBar = null;
    	}
    	
		RelvarEditorPanel relvarEditorView = rev.getCmdPanelOutput().getRelvarEditorView();
    	if (relvarEditorView != null) {
			toolBar = new RelvarEditorToolbar(this, relvarEditorView.getRelvarEditor());
			addZoom(toolBar.getToolBar());
    	} else
			toolBar = new CmdPanelToolbar(this, rev.getCmdPanelOutput()) {
				public void addAdditionalItemsBefore(ToolBar toolbar) {
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
				public void addAdditionalItemsAfter(ToolBar toolbar) {
					addZoom(toolbar);
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
		
		layout();
    }

	public DbTabContentRev(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

	    rev = new Rev(this, parentTab.getConnection(), parentTab.getCrashHandler(), "scratchpad", Rev.SAVE_AND_LOAD_BUTTONS) {
	    	@Override
	    	protected void changeToolbar() {
	    		makeToolbar(parentTab);
	    	}
	    };
	    
	    makeToolbar(parentTab);
	}

	private void zoom() {
		rev.zoom();
	}

	public void redisplayed() {
		rev.refresh();
	}

}
