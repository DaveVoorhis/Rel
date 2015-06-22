package org.reldb.dbrowser.ui.content.rel.var;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class RelvarEditorToolbar {
    
    private PreferenceChangeListener preferenceChangeListener;

	private ToolItem refreshBtn = null;
	private ToolItem goToInsertBtn = null;
	private ToolItem deleteBtn = null;

	private ToolBar toolBar;
	
	public RelvarEditorToolbar(Composite parent, RelvarEditor relvarEditor) {
		toolBar = new ToolBar(parent, SWT.None);

		addAdditionalItems(toolBar);
		
		refreshBtn = new ToolItem(toolBar, SWT.PUSH);
		refreshBtn.setToolTipText("Refresh");
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.refresh();
			}
		});

		goToInsertBtn = new ToolItem(toolBar, SWT.PUSH);
		goToInsertBtn.setToolTipText("Go to INSERT row");
		goToInsertBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.goToInsertRow();
			}
		});

		deleteBtn = new ToolItem(toolBar, SWT.PUSH);
		deleteBtn.setToolTipText("DELETE selected tuples");
		deleteBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarEditor.askDeleteSelected();
			}
		});

		setupIcons();

		preferenceChangeListener = new PreferenceChangeAdapter("RelvarEditorToolbar") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

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
	
	private Vector<ToolbarItem> additionalItems = new Vector<ToolbarItem>();
	
	/** Add an additional toolbar item. */
	protected void addAdditionalItem(ToolItem item, String iconName) {
		additionalItems.add(new ToolbarItem(item, iconName));
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItems(ToolBar toolBar) {}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

	private void setupIcons() {
		for (ToolbarItem tbi: additionalItems)
			tbi.getToolItem().setImage(IconLoader.loadIcon(tbi.getIconName()));
		refreshBtn.setImage(IconLoader.loadIcon("arrow_refresh"));
		goToInsertBtn.setImage(IconLoader.loadIcon("table_row_insert"));
		deleteBtn.setImage(IconLoader.loadIcon("table_row_delete"));
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

}
