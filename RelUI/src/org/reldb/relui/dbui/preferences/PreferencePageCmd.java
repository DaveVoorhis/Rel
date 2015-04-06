package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

/**
 * This class creates a preference page
 */
public class PreferencePageCmd extends FieldEditorPreferencePage {
	public static final String CMD_BROWSER_SWING = "browser.swing";

	/**
	 * Constructor
	 */
	public PreferencePageCmd() {
		setTitle("Command line");
		setDescription("Command-line mode settings.\n\nChanges will affect all new command-line tabs. Already-open command-line tabs will not be affected.");
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(CMD_BROWSER_SWING, "Use built-in (limited) output browser.", 
				getFieldEditorParent()));
	}

}
