package org.reldb.dbrowser.ui.content.cmd;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class CmdPanelToolbar {
    
    private PreferenceChangeListener preferenceChangeListener;

	private ToolItem clearOutputBtn = null;
	private ToolItem saveOutputAsHTMLBtn = null;
	private ToolItem saveOutputAsTextBtn = null;
	private ToolItem enhancedOutputToggle = null;
	private ToolItem showOkToggle = null;
	private ToolItem autoclearToggle = null;
	private ToolItem headingToggle = null;
	private ToolItem headingTypesToggle = null;

	private ToolBar toolBar;
	
	public CmdPanelToolbar(Composite parent, CmdPanelOutput cmdPanel) {
		toolBar = new ToolBar(parent, SWT.None);

		addAdditionalItems(toolBar);
		
		clearOutputBtn = new ToolItem(toolBar, SWT.PUSH);
		clearOutputBtn.setToolTipText("Clear");
		clearOutputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.clearOutput();
			}
		});

		saveOutputAsHTMLBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsHTMLBtn.setToolTipText("Save as HTML");
		saveOutputAsHTMLBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsHtml();
			}
		});

		saveOutputAsTextBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsTextBtn.setToolTipText("Save as text");
		saveOutputAsTextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsText();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		enhancedOutputToggle = new ToolItem(toolBar, SWT.CHECK);
		enhancedOutputToggle.setToolTipText("Display enhanced output");
		enhancedOutputToggle.setSelection(cmdPanel.getEnhancedOutput());
		enhancedOutputToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
				headingToggle.setEnabled(enhancedOutputToggle.getSelection());
				headingToggle.setSelection(headingToggle.getEnabled()
						&& cmdPanel.getHeadingVisible());
				headingTypesToggle.setEnabled(enhancedOutputToggle
						.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
						&& cmdPanel.getHeadingTypesVisible());
			}
		});

		showOkToggle = new ToolItem(toolBar, SWT.CHECK);
		showOkToggle.setToolTipText("Write 'Ok.' after execution");
		showOkToggle.setSelection(cmdPanel.getShowOk());
		showOkToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setShowOk(showOkToggle.getSelection());
			}
		});

		autoclearToggle = new ToolItem(toolBar, SWT.CHECK);
		autoclearToggle.setToolTipText("Automatically clear output");
		autoclearToggle.setSelection(cmdPanel.getAutoclear());
		autoclearToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setAutoclear(autoclearToggle.getSelection());
			}
		});

		headingToggle = new ToolItem(toolBar, SWT.CHECK);
		headingToggle.setToolTipText("Show relation headings");
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible()
				&& headingToggle.getEnabled());
		headingToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				headingTypesToggle.setEnabled(headingToggle.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
						&& cmdPanel.getHeadingTypesVisible());
				cmdPanel.setHeadingVisible(headingToggle.getSelection());
			}
		});

		headingTypesToggle = new ToolItem(toolBar, SWT.CHECK);
		headingTypesToggle
				.setToolTipText("Suppress attribute types in relation headings");
		headingTypesToggle.setEnabled(headingToggle.getSelection()
				&& enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible()
				&& headingTypesToggle.getEnabled());
		headingTypesToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setHeadingTypesVisible(headingTypesToggle
						.getSelection());
			}
		});

		setupIcons();

		preferenceChangeListener = new PreferenceChangeAdapter(
				"DbTabContentCmd") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};
		Preferences.addPreferenceChangeListener(
				PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
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
		clearOutputBtn.setImage(IconLoader.loadIcon("clearIcon"));
		saveOutputAsHTMLBtn.setImage(IconLoader.loadIcon("saveHTMLIcon"));
		saveOutputAsTextBtn.setImage(IconLoader.loadIcon("saveTextIcon"));
		enhancedOutputToggle.setImage(IconLoader.loadIcon("enhancedIcon"));
		showOkToggle.setImage(IconLoader.loadIcon("showOkIcon"));
		autoclearToggle.setImage(IconLoader.loadIcon("autoclearIcon"));
		headingToggle.setImage(IconLoader.loadIcon("headingIcon"));
		headingTypesToggle.setImage(IconLoader.loadIcon("typeSuppressIcon"));
	}

	public Control getToolBar() {
		return toolBar;
	}

}
