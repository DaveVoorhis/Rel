package org.reldb.dbrowser.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

/**
 * This class creates a preference page
 */
public class PreferencePageDisplay extends FieldEditorPreferencePage {
	// Names for preferences
	public static final String CMD_BROWSER_SWING = "browser.swing";

	/**
	 * Constructor
	 */
	public PreferencePageDisplay() {
		setTitle("Display");
		setDescription("Display settings.");
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(CMD_BROWSER_SWING, "Use an alternative output display to try to fix output problems.", getFieldEditorParent()));
	}

}
