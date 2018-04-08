package org.reldb.dbrowser.ui.content.filtersorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SearchAdvanced extends Composite implements Searcher {
	
	private static final String emptyFilterPrompt = "Click here to set filter criteria.";
	
	private FilterSorter filterSorter;
	private SearchAdvancedPanel filterer;
	private Label filterSpec;
	
	private PopupComposite popup;
	
	public SearchAdvanced(FilterSorter filterSorter, Composite contentPanel) {
		super(contentPanel, SWT.NONE);
		
		this.filterSorter = filterSorter;
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		filterSpec = new Label(this, SWT.NONE);
		filterSpec.setText(emptyFilterPrompt);
		filterSpec.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		filterSpec.addListener(SWT.MouseUp, e -> popup());		
		
		ToolBar toolBar = new ToolBar(this, SWT.NONE);
		
		ToolItem clear = new ToolItem(toolBar, SWT.PUSH);
		clear.addListener(SWT.Selection, e -> {
			filterSpec.setText(emptyFilterPrompt);
			filterSorter.refresh();
		});
		clear.setText("Clear");
		
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		this.addListener(SWT.Show, e -> {
			if (filterSpec.getText().equals(emptyFilterPrompt))
				popup();
		});
		
		constructPopup();
	}
	
	private void constructPopup() {
		popup = new PopupComposite(getShell());
		popup.setLayout(new GridLayout(1, false));
		
		filterer = new SearchAdvancedPanel(filterSorter.getAttributes(), popup);
		filterer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		Composite buttonPanel = new Composite(popup, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		buttonPanel.setLayout(new GridLayout(3, false));
		
		Button okButton = new Button(buttonPanel, SWT.PUSH);
		okButton.setText("Ok");
		okButton.addListener(SWT.Selection, e -> ok());
		
		Button cancelButton = new Button(buttonPanel, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, e -> {
			filterer.cancel();
			popup.hide();
		});
		
		Button clearButton = new Button(buttonPanel, SWT.PUSH);
		clearButton.setText("Clear");
		clearButton.addListener(SWT.Selection, e -> {
			filterer.clear();
			filterSpec.setText(emptyFilterPrompt);
			filterSorter.refresh();
		});
		
		popup.pack();		
	}

	public void ok() {
		filterer.ok();
		String spec = filterer.getWhereClause().trim();
		if (spec.length() == 0)
			filterSpec.setText(emptyFilterPrompt);
		else
			filterSpec.setText(spec);
		popup.hide();
		filterSorter.refresh();
	}
	
	private void popup() {
		if (!popup.isDisplayed())
			popup.show(toDisplay(0, 0));
	}

	public void clicked() {
		if (getVisible() == false && !filterSpec.getText().equals(emptyFilterPrompt))
			return;
		popup();
	}

	public String getQuery() {
		String spec = filterSpec.getText();
		return !spec.equals(emptyFilterPrompt)  ? " WHERE " + spec : "";
	}
	
}
