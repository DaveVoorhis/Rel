package org.reldb.dbrowser.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class IconLoader {
	public static Image loadIcon(String name) {
		boolean largeIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.LARGE_ICONS);
		return (largeIcons) ? loadIconLarge(name) : loadIconNormal(name);
	}
	
	public static Image loadIconNormal(String name) {
		Image imgBigRaw = ResourceManager.getPluginImage("RelUI", "icons/" + name + "@2x.png");
		Image imgSmallRaw = ResourceManager.getPluginImage("RelUI", "icons/" + name + ".png");
		if (imgBigRaw == null && imgSmallRaw == null) {
			imgBigRaw = ResourceManager.getPluginImage("RelUI", "icons/noimage@2x.png");
			imgSmallRaw = ResourceManager.getPluginImage("RelUI", "icons/noimage.png");
		}
		Image imgLarge = (imgBigRaw == null) ? imgSmallRaw : imgBigRaw;
		Image imgSmall = imgSmallRaw;
		final ImageDataProvider imageDataProvider = zoom -> {
			switch (zoom) {
			case 200:
				return imgLarge.getImageData();
			default:
				return imgSmall.getImageData();
			}
		};
		// TODO - should cache image in ResourceManager here!
		return new Image(Display.getCurrent(), imageDataProvider);
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = ResourceManager.getPluginImage("RelUI", "icons/" + name + "@2x.png");
		if (imgBig == null)
			return ResourceManager.getPluginImage("RelUI", "icons/" + name + ".png");
		return imgBig;
	}
}
