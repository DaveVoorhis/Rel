package org.reldb.relui.dbui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * This class creates a preference page
 */
public class PreferencePageGeneral extends PreferencePage {
	// Names for preferences
	private static final String ONE = "two.one";
	private static final String TWO = "two.two";
	private static final String THREE = "two.three";

	// The checkboxes
	private Button checkOne;
	private Button checkTwo;
	private Button checkThree;

	/**
	 * PrefPageTwo constructor
	 */
	public PreferencePageGeneral() {
		super("General");
		setDescription("Check the checks");
	}

	/**
	 * Creates the controls for this page
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Create three checkboxes
		checkOne = new Button(composite, SWT.CHECK);
		checkOne.setText("Check One");
		checkOne.setSelection(preferenceStore.getBoolean(ONE));

		checkTwo = new Button(composite, SWT.CHECK);
		checkTwo.setText("Check Two");
		checkTwo.setSelection(preferenceStore.getBoolean(TWO));

		checkThree = new Button(composite, SWT.CHECK);
		checkThree.setText("Check Three");
		checkThree.setSelection(preferenceStore.getBoolean(THREE));

		return composite;
	}

	/**
	 * Add buttons
	 * 
	 * @param parent - the parent composite
	 */
	protected void contributeButtons(Composite parent) {
		// Add a select all button
		Button selectAll = new Button(parent, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkOne.setSelection(true);
				checkTwo.setSelection(true);
				checkThree.setSelection(true);
			}
		});

		// Add a select all button
		Button clearAll = new Button(parent, SWT.PUSH);
		clearAll.setText("Clear All");
		clearAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				checkOne.setSelection(false);
				checkTwo.setSelection(false);
				checkThree.setSelection(false);
			}
		});

		// Add two columns to the parent's layout
		((GridLayout) parent.getLayout()).numColumns += 2;
	}

	/**
	 * Change the description label
	 */
	protected Label createDescriptionLabel(Composite parent) {
		Label label = null;
		String description = getDescription();
		if (description != null) {
			// Upper case the description
			description = description.toUpperCase();

			// Right-align the label
			label = new Label(parent, SWT.RIGHT);
			label.setText(description);
		}
		return label;
	}

	/**
	 * Called when user clicks Restore Defaults
	 */
	protected void performDefaults() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Reset the fields to the defaults
		checkOne.setSelection(preferenceStore.getDefaultBoolean(ONE));
		checkTwo.setSelection(preferenceStore.getDefaultBoolean(TWO));
		checkThree.setSelection(preferenceStore.getDefaultBoolean(THREE));
	}

	/**
	 * Called when user clicks Apply or OK
	 * 
	 * @return boolean
	 */
	public boolean performOk() {
		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		// Set the values from the fields
		if (checkOne != null)
			preferenceStore.setValue(ONE, checkOne.getSelection());
		if (checkTwo != null)
			preferenceStore.setValue(TWO, checkTwo.getSelection());
		if (checkThree != null)
			preferenceStore.setValue(THREE, checkThree.getSelection());

		// Return true to allow dialog to close
		return true;
	}
}
