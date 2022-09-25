package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.DbTab;

public class DbTabContentRel extends Composite {

	private CommandActivator tlitmShow;
	private CommandActivator tlitmEdit;
	private CommandActivator tlitmNew;
	private CommandActivator tlitmDrop;
	private CommandActivator tlitmDesign;
	private CommandActivator tlitmRename;
	private CommandActivator tlitmExport;
	private CommandActivator tlitmShowSystem;
    
	private RelPanel rel;
	
    private ToolBar tabToolBar = null;

    private ManagedToolbar mainToolBar;
    
	public DbTabContentRel(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		mainToolBar = new ManagedToolbar(this);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		mainToolBar.setLayoutData(fd_toolBar);
		
		rel = new RelPanel(parentTab, this, SWT.None) {
			@Override
			public void changeToolbar() {
				DbTabContentRel.this.changeToolbar();
			}
		};
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(mainToolBar, 4);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		rel.setLayoutData(fd_composite);
			
		new CommandActivator(Commands.Do.MakeBackup, mainToolBar, "safeIcon", SWT.None, "Make backup", e -> parentTab.makeBackup());
		tlitmShow = new CommandActivator(Commands.Do.Show, mainToolBar, "play", SWT.None, "Show", e -> rel.playItem());
		tlitmEdit = new CommandActivator(Commands.Do.Edit, mainToolBar, "item_edit", SWT.None, "Edit", e -> rel.editItem());
		tlitmNew = new CommandActivator(Commands.Do.New, mainToolBar, "item_add", SWT.None, "New", e -> rel.createItem());
		tlitmDrop = new CommandActivator(Commands.Do.Drop, mainToolBar, "item_delete", SWT.None, "Drop", e -> rel.dropItem());
		tlitmDesign = new CommandActivator(Commands.Do.Design, mainToolBar, "item_design", SWT.None, "Design", e -> rel.designItem());
		tlitmRename = new CommandActivator(Commands.Do.Rename, mainToolBar, "rename", SWT.None, "Rename", e -> rel.renameItem());
		tlitmExport = new CommandActivator(Commands.Do.Export, mainToolBar, "export", SWT.None, "Export", e -> rel.exportItem());
		tlitmShowSystem = new CommandActivator(Commands.Do.ShowSystemObjects, mainToolBar, "gears", SWT.CHECK, "Show system objects", e -> rel.setShowSystemObjects(tlitmShowSystem.getSelection()));
		
		tlitmShowSystem.setSelection(rel.getShowSystemObjects());
		
		rel.addDbTreeListener(new DbTreeListener() {
			public void select(DbTreeItem item) {
				tlitmShow.setEnabled(item.canPlay());
				tlitmEdit.setEnabled(item.canEdit());
				tlitmNew.setEnabled(item.canCreate());
				tlitmDrop.setEnabled(item.canDrop());
				tlitmDesign.setEnabled(item.canDesign());
				tlitmRename.setEnabled(item.canRename());
				tlitmExport.setEnabled(item.canExport());
			}
			public void tabChangeNotify() {
				changeToolbar();
			}
		});
		
		tlitmShow.setEnabled(false);
		tlitmEdit.setEnabled(false);
		tlitmNew.setEnabled(false);
		tlitmDrop.setEnabled(false);
		tlitmDesign.setEnabled(false);
		tlitmRename.setEnabled(false);
		tlitmExport.setEnabled(false);
	}

	public DbTreeTab getSelectedDbTreeTab() {
		CTabFolder tabs = rel.getTabFolder();
		CTabItem selectedTab = tabs.getSelection();
		if (selectedTab != null && selectedTab instanceof DbTreeTab)
			return (DbTreeTab)selectedTab;
		return null;
	}
	
	protected void changeToolbar() {
		if (tabToolBar != null) {
			tabToolBar.dispose();
			tabToolBar = null;
		}
		DbTreeTab selectedTab = getSelectedDbTreeTab();
		if (selectedTab != null) {
			DbTreeTab dbTreeTab = (DbTreeTab)selectedTab;
			if (dbTreeTab.isDisposed())
				return;
			tabToolBar = dbTreeTab.getToolBar(DbTabContentRel.this);
			if (tabToolBar != null) {
				FormData fd_toolBar = new FormData();
				fd_toolBar.left = new FormAttachment(mainToolBar);
				fd_toolBar.top = new FormAttachment(0);
				fd_toolBar.right = new FormAttachment(100);
				tabToolBar.setLayoutData(fd_toolBar);
			}
		}
		layout();
		activateMenu();
	}

	public void notifyIconSizeChange() {
	}

	public void redisplayed() {
		rel.redisplayed();
	}

	public void activateMenu() {
		DbTreeTab selectedTab = getSelectedDbTreeTab();
		if (selectedTab != null)
			selectedTab.activateMenu();
	}

}
