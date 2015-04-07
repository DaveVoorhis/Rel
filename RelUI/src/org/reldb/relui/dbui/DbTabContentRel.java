package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.relui.dbui.monitor.LogWin;
import org.reldb.relui.dbui.preferences.PreferenceChangeAdapter;
import org.reldb.relui.dbui.preferences.PreferenceChangeEvent;
import org.reldb.relui.dbui.preferences.PreferenceChangeListener;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;

public class DbTabContentRel extends Composite {

	private ToolItem tlitmBackup;
    
    private PreferenceChangeListener preferenceChangeListener;
	
	public DbTabContentRel(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(this, SWT.None);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);
		
		Button viewLog = new Button(this, SWT.None);
		viewLog.setText("Click here to view system log window.");
		viewLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogWin.open();
			}
		});
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		viewLog.setLayoutData(fd_composite);
	
		tlitmBackup = new ToolItem(toolBar, SWT.None);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentTab.makeBackup();
			}
		});
		
		setupIcons();
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTabContentRel") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.HALFRES_ICONS, preferenceChangeListener);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.HALFRES_ICONS, preferenceChangeListener);
		super.dispose();
	}
	
	private void setupIcons() {
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));		
	}

	public void notifyIconSizeChange() {
		setupIcons();
	}

}
