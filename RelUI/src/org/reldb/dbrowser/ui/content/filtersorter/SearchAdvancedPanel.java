package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.rel.client.Attribute;

public class SearchAdvancedPanel extends Composite {

	private FilterPanel filterPanel;
	private Text manualFilter;
	private boolean isManualOverride = false;
	private Vector<String[]> filterState = new Vector<>();
	
	public SearchAdvancedPanel(Vector<Attribute> attributes, Composite parent) {
		super(parent, SWT.NONE);		
		setLayout(new GridLayout(1, false));
		
		Composite filterDefinition = new Composite(this, SWT.NONE);
		StackLayout definitionStack = new StackLayout();
		filterDefinition.setLayout(definitionStack);
		
		filterPanel = new FilterPanel(attributes, filterDefinition, filterState);
		
		Composite manualPanel = new Composite(filterDefinition, SWT.NONE);
		manualPanel.setLayout(new GridLayout(2, false));
		Label prompt = new Label(manualPanel, SWT.NONE);
		prompt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		prompt.setText("Filter expression:");
		manualFilter = new Text(manualPanel, SWT.BORDER);
		manualFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button manualOverrideButton = new Button(this, SWT.CHECK);
		manualOverrideButton.setText("Manual override");
		manualOverrideButton.addListener(SWT.Selection, e -> {
			if (manualOverrideButton.getSelection()) {
				definitionStack.topControl = manualPanel;
				isManualOverride = true;
				if (manualFilter.getText().trim().length() == 0)
					manualFilter.setText(filterPanel.getWhereClauseInProgress());
			} else {
				definitionStack.topControl = filterPanel;
				isManualOverride = false;
			}
			filterDefinition.layout();
		});

		definitionStack.topControl = filterPanel;
		isManualOverride = false;
		manualOverrideButton.setSelection(false);
		
		filterDefinition.layout();
	}
	
	public String getWhereClause() {
		return isManualOverride ? manualFilter.getText() : filterPanel.getWhereClause();
	}

	public void ok() {
		if (!isManualOverride)
			filterPanel.ok();
	}

	public void cancel() {
		if (!isManualOverride)
			filterPanel.cancel();
	}

	public void clear() {
		if (isManualOverride) {
			manualFilter.setText("");
		} else
			filterPanel.clear();
	}

}
