package org.reldb.relui.dbui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;

public class IconLoader {
	public static Image loadIcon(String name) {
		boolean doubleSizedIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.DBL_ICONS);
		String fileName = name + ((doubleSizedIcons) ? "@2x" : "") + ".png";
		return ResourceManager.getPluginImage("RelUI", "icons/" + fileName);
	}
}
