package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends FieldEditorPreferencePage {
	public static final String DBL_ICONS = "dbl_size_icons";

	/**
	 * Constructor
	 */
	public PreferencePageGeneral() {
		setTitle("General");
		setDescription("General settings");
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(DBL_ICONS, "&Double-sized icons.", getFieldEditorParent()));
	}

}
