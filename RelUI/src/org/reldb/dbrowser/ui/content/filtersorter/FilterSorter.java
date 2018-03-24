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

	private enum SearchType {
		QUICK,
		ADVANCED
	};
	
	private Text quickFinder;
	private boolean wholeWordSearch = false;
	private boolean caseSensitiveSearch = false;
	
	private String baseExpression;
	private Vector<FilterSorterUpdate> updateListeners = new Vector<>();

	private SearchType search = SearchType.QUICK;
	
	private void fireUpdate() {
		for (FilterSorterUpdate listener: updateListeners)
			listener.update(this);
	}
	
	private Composite createQuickSearchPanel(Composite contentPanel) {		
		Composite panel = new Composite(contentPanel, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		panel.setLayout(layout);		
		
		quickFinder = new Text(panel, SWT.BORDER);
		quickFinder.addListener(SWT.Traverse, event -> {
			if (event.detail == SWT.TRAVERSE_RETURN) {
				fireUpdate();
			}
		});
		quickFinder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolBar quickFinderBar = new ToolBar(panel, SWT.NONE);
		
		ToolItem wholeWord = new ToolItem(quickFinderBar, SWT.PUSH);
		wholeWord.addListener(SWT.Selection, e -> {
			wholeWordSearch = !wholeWordSearch;
			wholeWord.setText(wholeWordSearch ? "Whole word" : "Any match");
			panel.layout();
			if (quickFinder.getText().trim().length() > 0)
				fireUpdate();
		});
		wholeWord.setText("Any match");

		ToolItem caseSensitive = new ToolItem(quickFinderBar, SWT.PUSH);
		caseSensitive.addListener(SWT.Selection, e -> {
			caseSensitiveSearch = !caseSensitiveSearch;
			caseSensitive.setText(caseSensitiveSearch ? "Case sensitive" : "Case insensitive");
			panel.layout();
			if (quickFinder.getText().trim().length() > 0)
				fireUpdate();
		});
		caseSensitive.setText("Case insensitive");
		
		ToolItem clear = new ToolItem(quickFinderBar, SWT.PUSH);
		clear.addListener(SWT.Selection, e -> {
			if (quickFinder.getText().trim().length() == 0)
				return;
			quickFinder.setText("");
			fireUpdate();
		});
		clear.setText("Clear");
		
		quickFinderBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		return panel;
	}

	private Composite createAdvancedSearchPanel(Composite contentPanel) {
		Composite panel = new Composite(contentPanel, SWT.NONE);
		panel.setLayout(new FillLayout());
		
		Label label = new Label(panel, SWT.NONE);
		label.setText("Advanced search panel goes here.");
		
		return panel;
	}

	private Composite createSortPanel(Composite contentPanel) {
		Composite panel = new Composite(contentPanel, SWT.NONE);
		panel.setLayout(new FillLayout());
		
		Label label = new Label(panel, SWT.NONE);
		label.setText("Sort panel goes here.");	
		
		return panel;
	}
	
	private String getQuickSearchWhereClause() {
		// TODO use SEARCH in Rel here
		return (quickFinder.getText().trim().length() > 0) ? " WHERE " + quickFinder.getText() : "";
	}
	
	private String getAdvancedSearchWhereClause() {
		return "";
	}
	
	private String getSortClause() {
		return "";
	}
	
	private String getWhereClause() {
		return ((search == SearchType.QUICK) ? getQuickSearchWhereClause() : getAdvancedSearchWhereClause())
				+ getSortClause();
	}
	
	public FilterSorter(Composite parent, int style, String baseExpression, FilterSorterState initialState) {
		super(parent, SWT.NONE);
		this.baseExpression = baseExpression;

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
		contentPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		StackLayout stack = new StackLayout();
		contentPanel.setLayout(stack);

		final Composite quickSearchPanel = createQuickSearchPanel(contentPanel);
		final Composite advancedSearchPanel = createAdvancedSearchPanel(contentPanel);
		final Composite sortPanel = createSortPanel(contentPanel);
		
		tltmQuickSearch.setToolTipText("Quick search.");
		tltmQuickSearch.setText("Q");
		tltmQuickSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(true);
			tltmAdvancedSearch.setSelection(false);
			tltmSort.setSelection(false);
			stack.topControl = quickSearchPanel;
			contentPanel.layout();
			search = SearchType.QUICK;
		});
		
		tltmAdvancedSearch.setToolTipText("Advanced search...");
		tltmAdvancedSearch.setText("A");
		tltmAdvancedSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(false);
			tltmAdvancedSearch.setSelection(true);
			tltmSort.setSelection(false);
			stack.topControl = advancedSearchPanel;
			contentPanel.layout();
			search = SearchType.ADVANCED;
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
		
		tltmQuickSearch.setSelection(true);
		stack.topControl = quickSearchPanel;
		
		if (initialState != null)
			quickFinder.setText(initialState.getRepresentation());
	}

	public FilterSorter(Composite parent, int style, String baseExpression) {
		this(parent, style, baseExpression, null);
	}

	public String getBaseExpression() {
		return baseExpression;
	}
	
	public String getQuery() {
		return "(" + baseExpression + ")" + getWhereClause();
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
