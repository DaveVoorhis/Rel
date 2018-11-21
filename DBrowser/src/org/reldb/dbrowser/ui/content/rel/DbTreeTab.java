package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.rel.exceptions.ExceptionFatal;

public class DbTreeTab extends CTabItem {
	
	protected RelPanel relPanel;
	protected DbTreeItem dbTreeItem;
	private String imageName;
	private PreferenceChangeListener preferenceChangeListener;
	
	public DbTreeTab(RelPanel parent, DbTreeItem dbTreeItem) {
		super(parent.getTabFolder(), SWT.NONE);
		this.relPanel = parent;
		this.dbTreeItem = dbTreeItem;
		setText(dbTreeItem.getTabName());
		relPanel.notifyTabCreated();
		preferenceChangeListener = new PreferenceChangeAdapter("DbTreeTab") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				reloadImage();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
		reloadImage();
	}
	
	public String getImageName() {
		return imageName;
	}
	
	@Deprecated
	public void setImage(Image image) {
		throw new ExceptionFatal("DbTreeTab: do not use setImage(Image); use setImageName(String).");
	}

	public void reloadImage() {
		super.setImage(IconLoader.loadIcon(imageName));
	}
	
	public void reload() {}
	
	public void ready() {
		relPanel.getTabFolder().setSelection(this);
		relPanel.fireDbTreeTabchangeEvent();
		if (isDisposed())
			return;
		Control control = getControl();
		if (!control.isDisposed())
			control.setFocus();
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);		
		super.dispose();
		relPanel.fireDbTreeTabchangeEvent();
	}
	
	public ToolBar getToolBar(Composite parent) {
		return null;
	}

	public boolean isSelfZoomable() {
		return false;
	}

	public void zoom() {}

	/** Override to allow a tab to reject a request to close. */
	public boolean canClose() {
		return true;
	}

	/** Override to activate additional menu items, other than those defined by the main toolbar returned by getToolBar(). */
	public void activateMenu() {}
}
