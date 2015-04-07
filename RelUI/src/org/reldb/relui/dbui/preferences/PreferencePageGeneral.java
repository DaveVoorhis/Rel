package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends FieldEditorPreferencePage {
	public static final String HALFRES_ICONS = "general.halfres_icons";

	/**
	 * Constructor
	 */
	public PreferencePageGeneral() {
		setTitle("General");
		setDescription("General settings.");
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(HALFRES_ICONS, "&Half-resolution icons.  On some platforms, this will show smaller icons.", getFieldEditorParent()));
	}

}
