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
    
	private static class CommandActivatorItem {
		private CommandActivator item;
		private String iconName;
		public CommandActivatorItem(CommandActivator item, String iconName) {
			this.item = item;
			this.iconName = iconName;
		}
		CommandActivator getItem() {return item;}
		String getIconName() {return iconName;}
	}
	
	private Vector<CommandActivatorItem> items = new Vector<CommandActivatorItem>();
	
	public CommandActivator addItem(Class<? extends MenuItem> menuClass, String toolTip, String iconName, int style) {
		CommandActivator item = new CommandActivator(menuClass, toolBar, style);
		item.setToolTipText(toolTip);
		item.setImage(IconLoader.loadIcon(iconName));
		items.add(new CommandActivatorItem(item, iconName));
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
		toolBar.addDisposeListener(e -> disposed());
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

	private void disposed() {
		for (CommandActivatorItem tbi: items)
			tbi.getItem().notifyToolbarDisposed();
	}

	private void setupIcons() {
		for (CommandActivatorItem tbi: items)
			tbi.getItem().setImage(IconLoader.loadIcon(tbi.getIconName()));
	}

	public ToolBar getToolBar() {
		return toolBar;
	}
}
