package org.reldb.dbrowser.ui.content.recent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class DbTabContentRecent extends Composite {
    
	private RecentPanel recentPanel;
	private DbTab parentTab;
    private PreferenceChangeListener preferenceChangeListener;
	
	public DbTabContentRecent(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		this.parentTab = parentTab;
		
		setLayout(new FormLayout());

		redisplayed();
		
		setBackgroundMode(SWT.INHERIT_FORCE);
		
		setupIcons();
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTabContentLRU") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		
		addListener(SWT.Resize, e -> {
			Image background = IconLoader.loadIcon("BirdSilhouette");
			Rectangle bounds = getBounds();
			if (bounds.width > 0 && bounds.height > 0)
				background = new Image(getDisplay(), background.getImageData().scaledTo(bounds.width, bounds.height));			
			setBackgroundImage(background);
		});
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}
	
	private void setupIcons() {
	}

	public void notifyIconSizeChange() {
		setupIcons();
	}

	public void redisplayed() {
		if (recentPanel != null)
			recentPanel.dispose();
		recentPanel = new RecentPanel(this, parentTab);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(0);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		recentPanel.setLayoutData(fd_composite);
		layout();
	}

}
