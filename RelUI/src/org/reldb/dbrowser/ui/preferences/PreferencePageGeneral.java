package org.reldb.dbrowser.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.Util;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends FieldEditorPreferencePage {
	public static final String LARGE_ICONS = "general.double_icons";
	public static final String DEFAULT_CMD_MODE = "general.default_cmd_mode";
	public static final String SKIP_DEFAULT_DB_LOAD = "general.skip_default_db_load";

	/**
	 * Constructor
	 */
	public PreferencePageGeneral() {
		setTitle("General");
		setDescription("General settings.");
	}

	protected void createFieldEditors() {
		String reloadPrompt = "";
		if (!Util.isMac())
			reloadPrompt = "  Restart after changing to see the full effect.";
		addField(new BooleanFieldEditor(LARGE_ICONS, "&Larger icons." + reloadPrompt, getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(DEFAULT_CMD_MODE, "Default to command-line mode.", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(SKIP_DEFAULT_DB_LOAD, "Do not automatically load user's default database.", getFieldEditorParent()));
	}

}
