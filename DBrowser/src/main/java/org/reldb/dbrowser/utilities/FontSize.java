package org.reldb.dbrowser.utilities;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.wb.swt.SWTResourceManager;

public class FontSize {
	public static Font getThisFontInNewSize(Font font, int size, int style) {
		FontData[] fontdata = font.getFontData();
		return SWTResourceManager.getFont(fontdata[0].getName(), size, style);
	}
}
