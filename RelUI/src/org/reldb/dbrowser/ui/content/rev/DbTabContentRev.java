package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.var.RelvarEditorToolbar;

public class DbTabContentRev extends Composite {
	
    private Rev rev;
    private ManagedToolbar toolBar = null;
    
	private void addZoom(ManagedToolbar toolbar) {
		toolbar.addSeparatorFill();
		// zoom
		toolbar.addItem("Zoom in or out", "view_fullscreen", SWT.PUSH).addSelectionListener(new SelectionAdapter() {
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
			addZoom(toolBar);
    	} else
			toolBar = new CmdPanelToolbar(this, rev.getCmdPanelOutput()) {
    			@Override
				public void addAdditionalItemsBefore() {
					// backup icon
					ToolItem tlitmBackup = addItem("Make backup", "safeIcon", SWT.PUSH);
					tlitmBackup.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							parentTab.makeBackup();
						}
					});
				}
    			@Override
				public void addAdditionalItemsAfter() {
					addZoom(toolBar);
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
