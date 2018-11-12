package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.Application;
import org.reldb.dbrowser.CommandActivator;
import org.reldb.dbrowser.IconLoader;
import org.reldb.dbrowser.MenuItemWithToolbar;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class DbTabContentRel extends Composite {

	private CommandActivator tlitmBackup;
	private CommandActivator tlitmShow;
	private CommandActivator tlitmEdit;
	private CommandActivator tlitmNew;
	private CommandActivator tlitmDrop;
	private CommandActivator tlitmDesign;
	private CommandActivator tlitmRename;
	private CommandActivator tlitmExport;
	private CommandActivator tlitmShowSystem;
    
	private RelPanel rel;
	
    private PreferenceChangeListener preferenceChangeListener;
	
    private ToolBar tabToolBar = null;

    private ToolBar mainToolBar;
    
	public DbTabContentRel(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		mainToolBar = new ToolBar(this, SWT.None);
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
			
		tlitmBackup = new CommandActivator(Application.getBackupMenuItem(), mainToolBar, SWT.None);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.addListener(SWT.Selection, e -> parentTab.makeBackup());
		
		tlitmShow = new CommandActivator(Application.getShowMenuItem(), mainToolBar, SWT.None);
		tlitmShow.setToolTipText("Show");
		tlitmShow.addListener(SWT.Selection, e -> rel.playItem());
		
		tlitmEdit = new CommandActivator(Application.getEditMenuItem(), mainToolBar, SWT.None);
		tlitmEdit.setToolTipText("Edit");
		tlitmEdit.addListener(SWT.Selection, e -> rel.editItem());
		
		tlitmNew = new CommandActivator(Application.getNewMenuItem(), mainToolBar, SWT.None);
		tlitmNew.setToolTipText("New");
		tlitmNew.addListener(SWT.Selection, e -> rel.createItem());
		
		tlitmDrop = new CommandActivator(Application.getDropMenuItem(), mainToolBar, SWT.None);
		tlitmDrop.setToolTipText("Drop");
		tlitmDrop.addListener(SWT.Selection, e -> rel.dropItem());
		
		tlitmDesign = new CommandActivator(Application.getDesignMenuItem(), mainToolBar, SWT.None);
		tlitmDesign.setToolTipText("Design");
		tlitmDesign.addListener(SWT.Selection, e -> rel.designItem());
		
		tlitmRename = new CommandActivator(Application.getRenameMenuItem(), mainToolBar, SWT.None);
		tlitmRename.setToolTipText("Rename");
		tlitmRename.addListener(SWT.Selection, e -> rel.renameItem());
		
		tlitmExport = new CommandActivator(Application.getExportMenuItem(), mainToolBar, SWT.None);
		tlitmExport.setToolTipText("Export");
		tlitmExport.addListener(SWT.Selection, e -> rel.exportItem());
		
		tlitmShowSystem = new CommandActivator(Application.getShowSystemObjectsMenuItem(), mainToolBar, SWT.CHECK);
		tlitmShowSystem.setToolTipText("Show system objects");
		tlitmShowSystem.setSelection(rel.getShowSystemObjects());
		tlitmShowSystem.addListener(SWT.Selection, e -> rel.setShowSystemObjects(tlitmShowSystem.getSelection()));
		
		setupIcons();
		
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
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTabContentRel") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		
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
		MenuItemWithToolbar.clear();
		if (tabToolBar != null) {
			tabToolBar.dispose();
			tabToolBar = null;
		}
		DbTreeTab selectedTab = getSelectedDbTreeTab();
		if (selectedTab != null) {
			tabToolBar = ((DbTreeTab)selectedTab).getToolBar(DbTabContentRel.this);
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
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}
	
	private void setupIcons() {
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));
		tlitmShow.setImage(IconLoader.loadIcon("play"));
		tlitmEdit.setImage(IconLoader.loadIcon("item_edit"));
		tlitmNew.setImage(IconLoader.loadIcon("item_add"));
		tlitmDrop.setImage(IconLoader.loadIcon("item_delete"));
		tlitmDesign.setImage(IconLoader.loadIcon("item_design"));
		tlitmRename.setImage(IconLoader.loadIcon("rename"));
		tlitmExport.setImage(IconLoader.loadIcon("export"));
		tlitmShowSystem.setImage(IconLoader.loadIcon("gears"));
	}

	public void notifyIconSizeChange() {
		setupIcons();
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
