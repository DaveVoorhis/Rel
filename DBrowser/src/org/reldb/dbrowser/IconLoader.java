package org.reldb.dbrowser;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class IconLoader {
	private final static String base = "/org/reldb/dbrowser/icons/";
	
	public static Image loadIcon(String name) {
		boolean largeIcons = Preferences.getPreferenceBoolean(PreferencePageGeneral.LARGE_ICONS);
		return (largeIcons) ? loadIconLarge(name) : loadIconNormal(name);
	}
	
	public static Image loadIconNormal(String name) {
		Image imgBigRaw = SWTResourceManager.getImageOrNull(base + name + "@2x.png");
		Image imgSmallRaw = SWTResourceManager.getImageOrNull(base + name + ".png");
		if (imgBigRaw == null && imgSmallRaw == null) {
			imgBigRaw = SWTResourceManager.getImage(base + "noimage@2x.png");
			imgSmallRaw = SWTResourceManager.getImage(base + "noimage.png");
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
		// TODO - should cache image in SWTResourceManager here!
		return new Image(Display.getCurrent(), imageDataProvider);
	}
	
	public static Image loadIconLarge(String name) {
		Image imgBig = SWTResourceManager.getImageOrNull(base + name + "@2x.png");
		if (imgBig == null)
			return SWTResourceManager.getImage(base + name + ".png");
		return imgBig;
	}
}
