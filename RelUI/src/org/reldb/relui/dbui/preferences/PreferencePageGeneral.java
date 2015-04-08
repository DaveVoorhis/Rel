package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.Util;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends FieldEditorPreferencePage {
	public static final String LARGE_ICONS = "general.halfres_icons";

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
	}

}
