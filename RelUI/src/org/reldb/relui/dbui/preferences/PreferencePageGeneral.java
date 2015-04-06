package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends FieldEditorPreferencePage {
	// Names for preferences
	public static final String GENERAL_FONT = "font";
	public static final String DBL_ICONS = "dbl_size_icons";

	/**
	 * Constructor
	 */
	public PreferencePageGeneral() {
		setTitle("General");
		setDescription("General settings");
	}

	protected void createFieldEditors() {
		addField(new FontFieldEditor(GENERAL_FONT, "&Font:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(DBL_ICONS, "&Double-sized icons.", getFieldEditorParent()));
	}

}
