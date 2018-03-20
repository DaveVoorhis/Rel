package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class FilterSorter extends Composite {

	@FunctionalInterface
	public interface FilterSorterUpdate {
		public void update(FilterSorter originator);
	}
	
	private Composite quickSearchPanel;
	private Text quickFinder;
	
	private Label advancedSearchPanel;
	private Label sortPanel;
	private String expression;
	private Vector<FilterSorterUpdate> updateListeners = new Vector<>();
	
	private void fireUpdate() {
		for (FilterSorterUpdate listener: updateListeners)
			listener.update(this);
	}
	
	public FilterSorter(Composite parent, int style, String baseExpression, FilterSorterState initialState) {
		super(parent, style);
		this.expression = baseExpression;

		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem tltmQuickSearch = new ToolItem(toolBar, SWT.CHECK);
		ToolItem tltmAdvancedSearch = new ToolItem(toolBar, SWT.CHECK);
		ToolItem tltmSort = new ToolItem(toolBar, SWT.CHECK);
		
		Composite contentPanel = new Composite(this, SWT.NONE);
		contentPanel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		StackLayout stack = new StackLayout();
		contentPanel.setLayout(stack);

		tltmQuickSearch.setToolTipText("Quick search.");
		tltmQuickSearch.setText("Q");
		tltmQuickSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(true);
			tltmAdvancedSearch.setSelection(false);
			tltmSort.setSelection(false);
			stack.topControl = quickSearchPanel;
			contentPanel.layout();
		});
		
		tltmAdvancedSearch.setToolTipText("Advanced search...");
		tltmAdvancedSearch.setText("A");
		tltmAdvancedSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(false);
			tltmAdvancedSearch.setSelection(true);
			tltmSort.setSelection(false);
			stack.topControl = advancedSearchPanel;
			contentPanel.layout();
		});
		
		tltmSort.setToolTipText("Sort...");
		tltmSort.setText("S");
		tltmSort.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(false);
			tltmAdvancedSearch.setSelection(false);
			tltmSort.setSelection(true);
			stack.topControl = sortPanel;
			contentPanel.layout();
		});

		quickSearchPanel = new Composite(contentPanel, SWT.NONE);
		quickSearchPanel.setLayout(new FillLayout());
		
		quickFinder = new Text(quickSearchPanel, SWT.BORDER);
		quickFinder.addListener(SWT.Traverse, event -> {
			if (event.detail == SWT.TRAVERSE_RETURN) {
				fireUpdate()	;
			}
		});

		advancedSearchPanel = new Label(contentPanel, SWT.NONE);
		advancedSearchPanel.setText("Advanced search panel goes here.");
		
		sortPanel = new Label(contentPanel, SWT.NONE);
		sortPanel.setText("Sort panel goes here.");	
		
		tltmQuickSearch.setSelection(true);
		stack.topControl = quickSearchPanel;
	}

	public FilterSorter(Composite parent, int style, String baseExpression) {
		this(parent, style, baseExpression, null);
	}

	public String getExpression() {
		return expression;
	}
	
	public String getQuery() {
		return "(" + expression + ")" + ((quickFinder.getText().trim().length() > 0) ? " WHERE " + quickFinder.getText() : "");
	}
	
	public void addUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.add(updateListener);
	}
	
	public void removeUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.remove(updateListener);
	}

	public FilterSorterState getState() {
		return new FilterSorterState(quickFinder.getText());
	}

}
