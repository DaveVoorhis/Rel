package org.reldb.dbrowser.ui.preferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.version.Version;

public class Preferences {
	private static PreferenceStore preferences = null;
	private static HashMap<String, HashSet<PreferenceChangeListener>> preferenceListeners;
	
	public static PreferenceStore getPreferences() {
		if (preferences == null) {
			preferences = new PreferenceStore(Version.getPreferencesRepositoryName());
			try {
				preferences.load();
			} catch (IOException e) {
				System.out.println("Preferences: Creating new preferences.");
			}
			preferenceListeners = new HashMap<String, HashSet<PreferenceChangeListener>>();
			preferences.addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					dispatchPreferenceChangeEvent(event.getProperty(), event.getNewValue());
				}
			});		
		}
		return preferences;
	}

	public static void setPreference(String name, Rectangle rect) {
		PreferenceStore prefs = getPreferences();
		prefs.setValue(name + "_x", rect.x);
		prefs.setValue(name + "_y", rect.y);
		prefs.setValue(name + "_width", rect.width);
		prefs.setValue(name + "_height", rect.height);
		save();
	}
	
	public static void setPreference(String name, String[] value) {
		int n = 0;
		for (String string: value)
			getPreferences().setValue(name + "[" + (n++) + "]", string);
		while (true) {
			String string = getPreferences().getString(name + "[" + n + "]");
			if (string.length() == 0)
				break;
			else
				getPreferences().setToDefault(name + "[" + n + "]");
			n++;
		}
		save();
	}
	
	public static void setPreference(String name, String value) {
		getPreferences().setValue(name, value);
		save();
	}
	
	public static void setPreference(String name, boolean value) {
		getPreferences().setValue(name, value);
		save();		
	}
	
	public static void setPreference(String name, int value) {
		getPreferences().setValue(name, value);
		save();		
	}

	public static Rectangle getPreferenceRectangle(String name) {
		PreferenceStore prefs = getPreferences();
		int x = prefs.getInt(name + "_x");
		int y = prefs.getInt(name + "_y");
		int width = prefs.getInt(name + "_width");
		int height = prefs.getInt(name + "_height");
		return new Rectangle(x, y, width, height);
	}
	
	public static String[] getPreferenceStringArray(String name) {
		int n = 0;
		Vector<String> strings = new Vector<String>();
		while (true) {
			String string = getPreferences().getString(name + "[" + (n++) + "]");
			if (string.length() == 0)
				break;
			else
				strings.add(string);
		}
		return strings.toArray(new String[0]);
	}
	
	public static String getPreferenceString(String name) {
		return getPreferences().getString(name);
	}
	
	public static boolean getPreferenceBoolean(String name) {
		return getPreferences().getBoolean(name);
	}
	
	public static int getPreferenceInteger(String name) {
		return getPreferences().getInt(name);
	}

	public static FontData[] getPreferenceFont(String name) {
		return PreferenceConverter.basicGetFontData(getPreferences().getString(name));
	}
	
	public static Font getPreferenceFont(Display display, String name) {
		FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		FontData[] fonts = fontRegistry.filterData(getPreferenceFont(name), display);
		if (fonts == null)
			return fontRegistry.defaultFont();
		return new Font(display, fonts);
	}
	
	private static void dispatchPreferenceChangeEvent(String name, Object newValue) {
		HashSet<PreferenceChangeListener> listeners = preferenceListeners.get(name);
		if (listeners == null)
			return;
		for (PreferenceChangeListener listener: listeners)
			try {
				listener.preferenceChange(new PreferenceChangeEvent(name));
			} catch (Exception e) {
				System.out.println("Preferences: exception notifying listener " + listener.toString() + ": " + e);
				e.printStackTrace();
			}
	}
	
	public static void addPreferenceChangeListener(String name, PreferenceChangeListener listener) {
		HashSet<PreferenceChangeListener> listeners = preferenceListeners.get(name);
		if (listeners == null) {
			listeners = new HashSet<PreferenceChangeListener>();
			preferenceListeners.put(name, listeners);
		}
		listeners.add(listener);
	}
	
	public static void removePreferenceChangeListener(String name, PreferenceChangeListener listener) {
		HashSet<PreferenceChangeListener> listeners = preferenceListeners.get(name);
		if (listeners == null)
			return;
		listeners.remove(listener);
		if (listeners.size() == 0)
			preferenceListeners.remove(name);
	}
	
	private PreferenceDialog preferenceDialog;

	public Preferences(Shell parent) {
		PreferenceManager preferenceManager = new PreferenceManager();
		
		PreferenceNode general = new PreferenceNode("General", new PreferencePageGeneral());
		preferenceManager.addToRoot(general);

		PreferenceNode cmd = new PreferenceNode("Command line", new PreferencePageCmd());
		preferenceManager.addToRoot(cmd);
		
		PreferenceNode display = new PreferenceNode("Display", new PreferencePageDisplay());
		preferenceManager.addToRoot(display);
		
		preferenceDialog = new PreferenceDialog(parent, preferenceManager);
		preferenceDialog.setPreferenceStore(preferences);
 	}

	private static void save() {
		try {
			preferences.save();
		} catch (IOException e) {
			System.out.println("Preferences: Unable to save preferences: " + e);
		}		
	}
	
	public void show() {
		preferenceDialog.open();
		save();
	}
}
