package org.reldb.rel.dbrowser.utilities;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Manager of user preferences.
 */
public class Preferences {
	private static Preferences preferences = new Preferences();
	private java.util.ArrayList<InputOutputFontChangeListener> inputOutputFontChangeListenerList = new java.util.ArrayList<InputOutputFontChangeListener>();

	public static Preferences getInstance() {
		return preferences;
	}

	private Preferences() {
	}

	public interface InputOutputFontChangeListener extends java.util.EventListener {
		public void fontChanged(java.awt.Font font);
	}

	public void addInputOutputFontChangeListener(InputOutputFontChangeListener listener) {
		inputOutputFontChangeListenerList.add(listener);
	}

	public void removeInputOutputFontChangeListener(InputOutputFontChangeListener listener) {
		inputOutputFontChangeListenerList.remove(listener);
	}

	public java.awt.Font getInputOutputFont() {
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(getClass());
		String family = userPreferences.get("INPUT_OUTPUT_FONT_FAMILY", "Arial");
		int size = userPreferences.getInt("INPUT_OUTPUT_FONT_SIZE", 12);
		return new java.awt.Font(family, java.awt.Font.PLAIN, size);
	}

	public void setInputOutputFont(java.awt.Font font) {
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(getClass());
		userPreferences.put("INPUT_OUTPUT_FONT_FAMILY", font.getFamily());
		userPreferences.putInt("INPUT_OUTPUT_FONT_SIZE", font.getSize());
		fireInputOutputFontChange(font);
	}

	public void fireInputOutputFontChange(java.awt.Font font) {
		for (int i = 0; i < inputOutputFontChangeListenerList.size(); ++i) {
			InputOutputFontChangeListener listener = (InputOutputFontChangeListener) inputOutputFontChangeListenerList.get(i);
			listener.fontChanged(font);
		}
	}

	public void obtainMainWindowPositionAndState(javax.swing.JFrame frame, int defaultX, int defaultY, int defaultWidth, int defaultHeight) {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(frame.getClass());
		String name = frame.getName();
		int windowX = userPreferences.getInt(name + "_WINDOW_POSITION_X", defaultX);
		int windowY = userPreferences.getInt(name + "_WINDOW_POSITION_Y", defaultY);
		int windowWidth = userPreferences.getInt(name + "_WINDOW_WIDTH", defaultWidth);
		int windowHeight = userPreferences.getInt(name + "_WINDOW_HEIGHT", defaultHeight);
		if (windowX < 0 || windowX > screenSize.width)
			windowX = defaultX;
		if (windowY < 0 || windowY > screenSize.height)
			windowY = defaultY;
		if (windowWidth < 10 || windowX + windowWidth > screenSize.width)
			windowWidth = defaultWidth;
		if (windowHeight < 10 || windowY + windowHeight > screenSize.height)
			windowHeight = defaultHeight;
		int state = userPreferences.getInt(name + "_WINDOW_STATE", javax.swing.JFrame.NORMAL);
		frame.setSize(windowWidth, windowHeight);
		frame.setLocation(windowX, windowY);
		frame.setExtendedState(state);		
	}
	
	public void obtainMainWindowPositionAndState(javax.swing.JFrame frame) {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int defaultWidth = (int)((double)screenSize.width * 0.9);
		int defaultHeight = (int)((double)screenSize.height * 0.9);
		int defaultX = screenSize.width/2 - defaultWidth/2;
		int defaultY = screenSize.height/2 - defaultHeight/2;
		obtainMainWindowPositionAndState(frame, defaultX, defaultY, defaultWidth, defaultHeight);
	}
	
	public void preserveMainWindowPositionAndState(javax.swing.JFrame frame) {
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(frame.getClass());
		Point position = frame.getLocationOnScreen();
		Dimension dimensions = frame.getSize();
		int state = frame.getExtendedState();
		if ((state & javax.swing.JFrame.ICONIFIED) != 0)
			state = javax.swing.JFrame.NORMAL;
		String name = frame.getName();
		userPreferences.putInt(name + "_WINDOW_POSITION_X", position.x);		
		userPreferences.putInt(name + "_WINDOW_POSITION_Y", position.y);
		userPreferences.putInt(name + "_WINDOW_WIDTH", dimensions.width);
		userPreferences.putInt(name + "_WINDOW_HEIGHT", dimensions.height);	
		userPreferences.putInt(name + "_WINDOW_STATE", state);		
	}
	
	public String getJavaCommand() {
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(getClass());
		return userPreferences.get("JAVA_COMMAND", "java");
	}
	
	public void setJavaCommand(String command) {
		java.util.prefs.Preferences userPreferences = java.util.prefs.Preferences.userNodeForPackage(getClass());
		userPreferences.put("JAVA_COMMAND", command);		
	}
}