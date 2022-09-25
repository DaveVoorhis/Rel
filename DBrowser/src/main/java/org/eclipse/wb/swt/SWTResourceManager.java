/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wb.swt;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Based on SWTResourceManager originally by:
 * @author scheglov_ke
 * @author Dan Rubel
 * 
 * Modified by:
 * @author Dave Voorhis
 */
public class SWTResourceManager {
	
	// Map RGB values to ColorS.
	private static Map<RGB, Color> m_colorMap = new HashMap<RGB, Color>();
	
	/**
	 * Returns the system {@link Color} matching the specific ID.
	 * 
	 * @param systemColorID
	 *            the ID value for the color
	 * @return the system {@link Color} matching the specific ID
	 */
	public static Color getColor(int systemColorID) {
		Display display = Display.getCurrent();
		return display.getSystemColor(systemColorID);
	}
	
	/**
	 * Returns a {@link Color} given its red, green and blue component values.
	 * 
	 * @param r
	 *            the red component of the color
	 * @param g
	 *            the green component of the color
	 * @param b
	 *            the blue component of the color
	 * @return the {@link Color} matching the given red, green and blue component values
	 */
	public static Color getColor(int r, int g, int b) {
		return getColor(new RGB(r, g, b));
	}
	
	/**
	 * Returns a {@link Color} given its RGB value.
	 * 
	 * @param rgb
	 *            the {@link RGB} value of the color
	 * @return the {@link Color} matching the RGB value
	 */
	public static Color getColor(RGB rgb) {
		Color color = m_colorMap.get(rgb);
		if (color == null) {
			Display display = Display.getCurrent();
			color = new Color(display, rgb);
			m_colorMap.put(rgb, color);
		}
		return color;
	}
	
	/**
	 * Dispose of all the cached {@link Color}'s.
	 */
	public static void disposeColors() {
		for (Color color : m_colorMap.values()) {
			color.dispose();
		}
		m_colorMap.clear();
	}
	
	// Maps image paths to images.
	private static Map<String, Image> m_imageMap = new HashMap<String, Image>();

	/**
	 * Returns an {@link Image} encoded by the specified {@link InputStream}.
	 * 
	 * @param stream
	 *            the {@link InputStream} encoding the image data
	 * @return the {@link Image} encoded by the specified input stream
	 */
	protected static Image getImage(InputStream stream) throws IOException {
		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}
	
	/**
	 * Returns an {@link Image} stored in the file at the specified path.
	 * 
	 * @param path
	 *            the path to the image file
	 * @return the {@link Image} stored in the file at the specified path or null if not found.
	 */
	public static Image getImageOrNull(String path) {
		Image image = m_imageMap.get(path);
		if (image == null) {
			try {
				InputStream imageStream = SWTResourceManager.class.getResourceAsStream(path);
				image = getImage(imageStream);
				m_imageMap.put(path, image);
			} catch (Exception e) {
				image = null;
			}
		}
		return image;
	}
	
	/**
	 * Returns an {@link Image} stored in the file at the specified path.
	 * 
	 * @param path
	 *            the path to the image file
	 * @return the {@link Image} stored in the file at the specified path, or a missing image image.
	 */
	public static Image getImage(String path) {
		Image image = getImageOrNull(path);
		if (image == null) {
			image = getMissingImage();
			m_imageMap.put(path, image);
		}
		return image;
	}
	
	private static final int MISSING_IMAGE_SIZE = 10;
	
	/**
	 * @return the small {@link Image} that can be used as placeholder for missing image.
	 */
	public static Image getMissingImage() {
		Image image = new Image(Display.getCurrent(), MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
		//
		GC gc = new GC(image);
		gc.setBackground(getColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
		gc.dispose();
		//
		return image;
	}
	
	/**
	 * Dispose all of the cached {@link Image}'s.
	 */
	public static void disposeImages() {
		// dispose loaded images
		{
			for (Image image : m_imageMap.values()) {
				image.dispose();
			}
			m_imageMap.clear();
		}
	}
	
	// Maps font names to fonts.
	private static Map<String, Font> m_fontMap = new HashMap<String, Font>();
	
	// Maps fonts to their bold versions.
	private static Map<Font, Font> m_fontToBoldFontMap = new HashMap<Font, Font>();
	
	/**
	 * Returns a {@link Font} based on its name, height and style.
	 * 
	 * @param name
	 *            the name of the font
	 * @param height
	 *            the height of the font
	 * @param style
	 *            the style of the font
	 * @return {@link Font} The font matching the name, height and style
	 */
	public static Font getFont(String name, int height, int style) {
		return getFont(name, height, style, false, false);
	}
	
	/**
	 * Returns a {@link Font} based on its name, height and style. Windows-specific strikeout and underline
	 * flags are also supported.
	 * 
	 * @param name
	 *            the name of the font
	 * @param size
	 *            the size of the font
	 * @param style
	 *            the style of the font
	 * @param strikeout
	 *            the strikeout flag (warning: Windows only)
	 * @param underline
	 *            the underline flag (warning: Windows only)
	 * @return {@link Font} The font matching the name, height, style, strikeout and underline
	 */
	public static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {
		String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
		Font font = m_fontMap.get(fontName);
		if (font == null) {
			FontData fontData = new FontData(name, size, style);
			if (strikeout || underline) {
				try {
					Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT"); //$NON-NLS-1$
					Object logFont = FontData.class.getField("data").get(fontData); //$NON-NLS-1$
					if (logFont != null && logFontClass != null) {
						if (strikeout) {
							logFontClass.getField("lfStrikeOut").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
						}
						if (underline) {
							logFontClass.getField("lfUnderline").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
						}
					}
				} catch (Throwable e) {
					System.err.println("Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			font = new Font(Display.getCurrent(), fontData);
			m_fontMap.put(fontName, font);
		}
		return font;
	}
	
	/**
	 * Returns a bold version of the given {@link Font}.
	 * 
	 * @param baseFont
	 *            the {@link Font} for which a bold version is desired
	 * @return the bold version of the given {@link Font}
	 */
	public static Font getBoldFont(Font baseFont) {
		Font font = m_fontToBoldFontMap.get(baseFont);
		if (font == null) {
			FontData fontDatas[] = baseFont.getFontData();
			FontData data = fontDatas[0];
			font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
			m_fontToBoldFontMap.put(baseFont, font);
		}
		return font;
	}
	
	/**
	 * Dispose all of the cached {@link Font}'s.
	 */
	public static void disposeFonts() {
		// clear fonts
		for (Font font : m_fontMap.values()) {
			font.dispose();
		}
		m_fontMap.clear();
		// clear bold fonts
		for (Font font : m_fontToBoldFontMap.values()) {
			font.dispose();
		}
		m_fontToBoldFontMap.clear();
	}
	
	// Maps IDs to cursors.
	private static Map<Integer, Cursor> m_idToCursorMap = new HashMap<Integer, Cursor>();
	
	/**
	 * Returns the system cursor matching the specific ID.
	 * 
	 * @param id
	 *            int The ID value for the cursor
	 * @return Cursor The system cursor matching the specific ID
	 */
	public static Cursor getCursor(int id) {
		Integer key = Integer.valueOf(id);
		Cursor cursor = m_idToCursorMap.get(key);
		if (cursor == null) {
			cursor = new Cursor(Display.getDefault(), id);
			m_idToCursorMap.put(key, cursor);
		}
		return cursor;
	}
	
	/**
	 * Dispose all of the cached cursors.
	 */
	public static void disposeCursors() {
		for (Cursor cursor : m_idToCursorMap.values()) {
			cursor.dispose();
		}
		m_idToCursorMap.clear();
	}
	
	/**
	 * Dispose of cached objects and their underlying OS resources. This should only be called when the cached
	 * objects are no longer needed (e.g. on application shutdown).
	 */
	public static void dispose() {
		disposeColors();
		disposeImages();
		disposeFonts();
		disposeCursors();
	}
}