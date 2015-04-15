package org.reldb.dbrowser.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class IconLoader {
	public static Image loadIcon(String name) {
		boolean largeIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.LARGE_ICONS);
		return (largeIcons) ? loadIconLarge(name) : loadIconSmall(name);
	}
	
	public static Image loadIconSmall(String name) {
		return ResourceManager.getPluginImage("RelUI", "icons/" + name + ".png");
	}
	
	public static Image loadIconLarge(String name) {
		return ResourceManager.getPluginImage("RelUI", "icons/" + name + "@2x.png");		
	}
}
