package org.reldb.dbrowser.dbui.content.rev;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.reldb.dbrowser.dbui.DbTab;
import org.reldb.dbrowser.dbui.IconLoader;
import org.reldb.dbrowser.dbui.content.rev.core.Rev;
import org.reldb.dbrowser.dbui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.dbui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.dbui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.dbui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.dbui.preferences.Preferences;

public class DbTabContentRev extends Composite {

	private ToolItem tlitmBackup;
    
    private PreferenceChangeListener preferenceChangeListener;
	
	public DbTabContentRev(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(this, SWT.None);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);

	    Composite content = new Composite(parentTab.getParent(), SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(content);
	    Rev rev = new Rev(parentTab.getURL(), parentTab.getCrashHandler());
	    frame.add(rev);

	    FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		content.setLayoutData(fd_composite);
	
		tlitmBackup = new ToolItem(toolBar, SWT.None);
		tlitmBackup.setToolTipText("Make backup");
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
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}

	private void setupIcons() {
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));		
	}
	
	public void notifyIconSizeChange() {
		setupIcons();
	}

	public void redisplayed() {
	}

}
