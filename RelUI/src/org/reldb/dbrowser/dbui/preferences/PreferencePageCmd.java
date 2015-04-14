package org.reldb.dbrowser.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;

/**
 * This class creates a preference page
 */
public class PreferencePageCmd extends FieldEditorPreferencePage {
	// Names for preferences
	public static final String CMD_BROWSER_SWING = "browser.swing";
	public static final String CMD_FONT = "browser.font";

	/**
	 * Constructor
	 */
	public PreferencePageCmd() {
		setTitle("Command line");
		setDescription("Command-line mode settings.");
	}

	protected void createFieldEditors() {
		addField(new FontFieldEditor(CMD_FONT, "&Font:", "ABCabc0123", getFieldEditorParent()));
		addField(new BooleanFieldEditor(CMD_BROWSER_SWING, "Use an alternative output display to try to fix output problems.", getFieldEditorParent()));
	}

}
