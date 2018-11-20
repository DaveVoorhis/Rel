package org.reldb.dbrowser.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class ManagedToolbar extends ToolBar {    
    private PreferenceChangeListener preferenceChangeListener;
    
	public ManagedToolbar(Composite parent) {
		super(parent, SWT.NONE);
		preferenceChangeListener = new PreferenceChangeAdapter("ManagedToolbar") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				for (ToolItem item: getItems()) {
					if (item instanceof CommandActivator) {
						CommandActivator activator = (CommandActivator)item;
						activator.setImage(IconLoader.loadIcon(activator.getIconName()));
					}
				}
				layout();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public void addSeparator() {
		new ToolItem(this, SWT.SEPARATOR);
	}
	
	public void addSeparatorFill() {
		new ToolItem(this, SWT.SEPARATOR);
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();
	}

	public void checkSubclass() {}
}
