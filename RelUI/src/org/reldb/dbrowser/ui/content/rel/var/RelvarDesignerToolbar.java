package org.reldb.dbrowser.ui.content.rel.var;

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

public class RelvarDesignerToolbar {
    
    private PreferenceChangeListener preferenceChangeListener;

	private ToolItem refreshBtn = null;

	private ToolBar toolBar;
	
	public RelvarDesignerToolbar(Composite parent, RelvarDesigner relvarDesigner) {
		toolBar = new ToolBar(parent, SWT.None);
		
		refreshBtn = new ToolItem(toolBar, SWT.PUSH);
		refreshBtn.setToolTipText("Refresh");
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relvarDesigner.refresh();
			}
		});

		setupIcons();

		preferenceChangeListener = new PreferenceChangeAdapter("RelvarDesignerToolbar") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}

	private void setupIcons() {
		refreshBtn.setImage(IconLoader.loadIcon("arrow_refresh"));
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

}
