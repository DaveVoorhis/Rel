package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.ManagedToolbar;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class RelvarEditorToolbar implements ManagedToolbar {
    
    private PreferenceChangeListener preferenceChangeListener;

	private ToolItem refreshBtn = null;
	private ToolItem goToInsertBtn = null;
	private ToolItem deleteBtn = null;

	private ToolBar toolBar;
	
	public RelvarEditorToolbar(Composite parent, RelvarEditor relvarEditor) {
		toolBar = new ToolBar(parent, SWT.None);
		
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

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		toolBar.dispose();
	}

	private void setupIcons() {
		refreshBtn.setImage(IconLoader.loadIcon("arrow_refresh"));
		goToInsertBtn.setImage(IconLoader.loadIcon("table_row_insert"));
		deleteBtn.setImage(IconLoader.loadIcon("table_row_delete"));
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

}
