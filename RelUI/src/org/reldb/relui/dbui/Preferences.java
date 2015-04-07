package org.reldb.relui.dbui;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relui.dbui.preferences.PreferenceChangeEvent;
import org.reldb.relui.dbui.preferences.PreferenceChangeListener;
import org.reldb.relui.dbui.preferences.PreferencePageCmd;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;
import org.reldb.relui.version.Version;

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

	public static String getPreferenceString(String name) {
		return getPreferences().getString(name);
	}
	
	public static boolean getPreferenceBoolean(String name) {
		return getPreferences().getBoolean(name);
	}

	public static FontData[] getPreferenceFont(String name) {
		return PreferenceConverter.basicGetFontData(getPreferences().getString(name));
	}
	
	private static void dispatchPreferenceChangeEvent(String name, Object newValue) {
		HashSet<PreferenceChangeListener> listeners = preferenceListeners.get(name);
		if (listeners == null)
			return;
		for (PreferenceChangeListener listener: listeners)
			try {
				listener.preferenceChange(new PreferenceChangeEvent(name, newValue.toString()));
			} catch (Exception e) {
				System.out.println("Preferences: exception notifying listener " + listener.toString() + ": " + e);
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
		
		preferenceDialog = new PreferenceDialog(parent, preferenceManager);
		preferenceDialog.setPreferenceStore(preferences);
 	}

	public void show() {
		preferenceDialog.open();
		try {
			preferences.save();
		} catch (IOException e) {
			System.out.println("Preferences: Unable to save preferences: " + e);
		}
	}
}
