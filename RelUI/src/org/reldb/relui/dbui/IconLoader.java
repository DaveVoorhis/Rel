package org.reldb.relui.dbui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;

public class IconLoader {
	public static Image loadIcon(String name) {
		boolean halfSizedIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.HALFRES_ICONS);
		return (halfSizedIcons) ? loadIconSmall(name) : loadIconLarge(name);
	}
	
	public static Image loadIconSmall(String name) {
		return ResourceManager.getPluginImage("RelUI", "icons/" + name + ".png");
	}
	
	public static Image loadIconLarge(String name) {
		return ResourceManager.getPluginImage("RelUI", "icons/" + name + "@2x.png");		
	}
}
