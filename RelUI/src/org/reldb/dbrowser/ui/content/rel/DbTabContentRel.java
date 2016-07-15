package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class DbTabContentRel extends Composite {

	private ToolItem tlitmZoom;
	private ToolItem tlitmBackup;
	private ToolItem tlitmShow;
	private ToolItem tlitmNew;
	private ToolItem tlitmDrop;
	private ToolItem tlitmDesign;
	private ToolItem tlitmRename;
	private ToolItem tlitmShowSystem;
    
	private RelPanel rel;
	
    private PreferenceChangeListener preferenceChangeListener;
	
    private ToolBar tabToolBar = null;
    
	public DbTabContentRel(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		ToolBar mainToolBar = new ToolBar(this, SWT.None);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		mainToolBar.setLayoutData(fd_toolBar);
		
		rel = new RelPanel(parentTab, this, SWT.None);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(mainToolBar, 4);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		rel.setLayoutData(fd_composite);
		
		// zoom
		tlitmZoom = new ToolItem(mainToolBar, SWT.NONE);
		tlitmZoom.setToolTipText("Zoom in or out");
		tlitmZoom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.zoom();
			}
		});
		tlitmZoom.setEnabled(false);
	
		tlitmBackup = new ToolItem(mainToolBar, SWT.None);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentTab.makeBackup();
			}
		});
		
		tlitmShow = new ToolItem(mainToolBar, SWT.None);
		tlitmShow.setToolTipText("Show");
		tlitmShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.playItem();
			}
		});
				
		tlitmNew = new ToolItem(mainToolBar, SWT.None);
		tlitmNew.setToolTipText("New");
		tlitmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.createItem();
			}
		});
		
		tlitmDrop = new ToolItem(mainToolBar, SWT.None);
		tlitmDrop.setToolTipText("Drop");
		tlitmDrop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.dropItem();
			}
		});
		
		tlitmDesign = new ToolItem(mainToolBar, SWT.None);
		tlitmDesign.setToolTipText("Design");
		tlitmDesign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.designItem();
			}
		});
		
		tlitmRename = new ToolItem(mainToolBar, SWT.None);
		tlitmRename.setToolTipText("Rename");
		tlitmRename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.renameItem();
			}
		});
		
		tlitmShowSystem = new ToolItem(mainToolBar, SWT.CHECK);
		tlitmShowSystem.setToolTipText("Show system objects");
		tlitmShowSystem.setSelection(rel.getShowSystemObjects());
		tlitmShowSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rel.setShowSystemObjects(tlitmShowSystem.getSelection());
			}
		});
		
		setupIcons();
		
		rel.addDbTreeListener(new DbTreeListener() {
			public void select(DbTreeItem item) {
				tlitmShow.setEnabled(item.canPlay());
				tlitmNew.setEnabled(item.canCreate());
				tlitmDrop.setEnabled(item.canDrop());
				tlitmDesign.setEnabled(item.canDesign());
				tlitmRename.setEnabled(item.canRename());
			}
			public void tabChangeNotify() {
				if (tabToolBar != null) {
					tabToolBar.dispose();
					tabToolBar = null;
				}
				CTabFolder tabs = rel.getTabFolder();
				if (!tlitmZoom.isDisposed())
					tlitmZoom.setEnabled(tabs.getItemCount() > 0);
				CTabItem selectedTab = tabs.getSelection();
				if (selectedTab != null) {
					if (selectedTab instanceof DbTreeTab) {
						tabToolBar = ((DbTreeTab)selectedTab).getToolBar(DbTabContentRel.this);
						if (tabToolBar != null) {
							FormData fd_toolBar = new FormData();
							fd_toolBar.left = new FormAttachment(mainToolBar);
							fd_toolBar.top = new FormAttachment(0);
							fd_toolBar.right = new FormAttachment(100);
							tabToolBar.setLayoutData(fd_toolBar);
						}
					}
				}
				layout();
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
		tlitmNew.setEnabled(false);
		tlitmDrop.setEnabled(false);
		tlitmDesign.setEnabled(false);
		tlitmRename.setEnabled(false);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}
	
	private void setupIcons() {
		tlitmZoom.setImage(IconLoader.loadIcon("view_fullscreen"));
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));
		tlitmShow.setImage(IconLoader.loadIcon("play"));
		tlitmNew.setImage(IconLoader.loadIcon("item_add"));
		tlitmDrop.setImage(IconLoader.loadIcon("item_delete"));
		tlitmDesign.setImage(IconLoader.loadIcon("item_design"));
		tlitmRename.setImage(IconLoader.loadIcon("rename"));
		tlitmShowSystem.setImage(IconLoader.loadIcon("gears"));
	}

	public void notifyIconSizeChange() {
		setupIcons();
	}

	public void redisplayed() {
		rel.redisplayed();
	}

}
