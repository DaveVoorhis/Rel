package org.reldb.dbrowser.ui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.handlers.MenuItem;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class ManagedToolbar {    
    private PreferenceChangeListener preferenceChangeListener;
    private ToolBar toolBar;
    
	private static class ToolbarItem {
		private ToolItem toolItem;
		private String iconName;
		public ToolbarItem(ToolItem toolItem, String iconName) {
			this.toolItem = toolItem;
			this.iconName = iconName;
		}
		ToolItem getToolItem() {return toolItem;}
		String getIconName() {return iconName;}
	}
	
	private Vector<ToolbarItem> toolbarItems = new Vector<ToolbarItem>();
	
	public CommandActivator addItem(Class<? extends MenuItem> menuClass, String toolTip, String iconName, int style) {
		CommandActivator item = new CommandActivator(menuClass, toolBar, style);
		item.setToolTipText(toolTip);
		item.setImage(IconLoader.loadIcon(iconName));
		ToolbarItem toolbarItem = new ToolbarItem(item, iconName);
		toolbarItems.add(toolbarItem);
		return item;
	}
	
	public void addSeparator() {
		new ToolItem(toolBar, SWT.SEPARATOR);
	}
	
	public void addSeparatorFill() {
		new ToolItem(toolBar, SWT.SEPARATOR);
	}
	
	public ManagedToolbar(Composite parent) {
		toolBar = new ToolBar(parent, SWT.None);
		preferenceChangeListener = new PreferenceChangeAdapter("ManagedToolbar") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		toolBar.dispose();
	}

	private void setupIcons() {
		for (ToolbarItem tbi: toolbarItems)
			tbi.getToolItem().setImage(IconLoader.loadIcon(tbi.getIconName()));
	}

	public ToolBar getToolBar() {
		return toolBar;
	}
}
