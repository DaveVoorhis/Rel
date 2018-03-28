package org.reldb.dbrowser.ui.content.filtersorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Sorter extends Composite {
	
	private static final String emptySortPrompt = "Click here to set sort order.";
	
	private FilterSorter filterSorter;
	private Label sortSpec;
	
	public Sorter(FilterSorter filterSorter, Composite contentPanel) {
		super(contentPanel, SWT.NONE);
		
		this.filterSorter = filterSorter;
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);		

		sortSpec = new Label(this, SWT.NONE);
		sortSpec.setText(emptySortPrompt);
		sortSpec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		sortSpec.addListener(SWT.MouseUp, e -> popup());		
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		
		ToolItem clear = new ToolItem(toolBar, SWT.PUSH);
		clear.addListener(SWT.Selection, e -> {
			sortSpec.setText(emptySortPrompt);
			filterSorter.fireUpdate();
		});
		clear.setText("Clear");
		
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		this.addListener(SWT.Show, e -> {
			if (sortSpec.getText().equals(emptySortPrompt))
				popup();
		});
	}
	
	private void popup() {
		PopupComposite popup = new PopupComposite(getShell());
		popup.setLayout(new GridLayout(1, false));
		
		OrderPanel orderer = new OrderPanel(popup, SWT.NONE);
		orderer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		orderer.setAvailableAttributes(filterSorter.getAttributeNames());
		
		Composite buttonPanel = new Composite(popup, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		buttonPanel.setLayout(new GridLayout(2, false));
		
		Button okButton = new Button(buttonPanel, SWT.PUSH);
		okButton.setText("Ok");
		okButton.addListener(SWT.Selection, e -> {
			String spec = orderer.getText().trim();
			if (spec.length() == 0)
				sortSpec.setText(emptySortPrompt);
			else
				sortSpec.setText(spec);
			sortSpec.setText(orderer.getText());
			popup.close();
			filterSorter.fireUpdate();
		});
		
		Button cancelButton = new Button(buttonPanel, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, e -> {
			popup.close();
		});
		
		String sortSpecText = sortSpec.getText();
		if (!sortSpecText.equals(emptySortPrompt))
			orderer.setText(sortSpecText);
		
		popup.pack();
		popup.show(toDisplay(0, 0));
	}

	public void clicked() {
		if (getVisible() == false && !sortSpec.getText().equals(emptySortPrompt))
			return;
		popup();
	}

	public String getQuery() {
		String sortSpecText = sortSpec.getText();		
		return (!sortSpecText.equals(emptySortPrompt)) ? " ORDER (" + sortSpecText + ")" : "";
	}

	public void setState(String state) {
	}

	public String getState() {
		return "";
	}
	
}