package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Attribute;

public class FilterSorter extends Composite {

	@FunctionalInterface
	public interface FilterSorterUpdate {
		public void update(FilterSorter originator);
	}
		
	private String baseExpression;
	
	private Vector<FilterSorterUpdate> updateListeners = new Vector<>();
	
	private Searcher searcher;
	private Sorter sorter;

	private DbConnection dbConnection;

	private Searcher lastSearch = null;
		
	public void refresh() {
		for (FilterSorterUpdate listener: updateListeners)
			listener.update(this);
	}
	
	public FilterSorter(Composite parent, int style, String baseExpression, DbConnection dbConnection) {
		super(parent, style);
		this.baseExpression = baseExpression;
		this.dbConnection = dbConnection;
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		
		ManagedToolbar toolBar = new ManagedToolbar(this);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		CommandActivator tltmQuickSearch = new CommandActivator(null, toolBar, "filtersorter_search", SWT.CHECK, "Quick search.", null);
		CommandActivator tltmAdvancedSearch = new CommandActivator(null, toolBar, "filtersorter_filter", SWT.CHECK, "Advanced search...", null);
		CommandActivator tltmSort = new CommandActivator(null, toolBar, "filtersorter_sort", SWT.CHECK, "Sort...", null);
		
		Composite contentPanel = new Composite(this, SWT.NONE);
		contentPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		StackLayout stack = new StackLayout();
		contentPanel.setLayout(stack);

		SearchQuick quickSearchPanel = new SearchQuick(this, contentPanel);
		SearchAdvanced advancedSearchPanel = new SearchAdvanced(this, contentPanel);
		sorter = new Sorter(this, contentPanel);
		
		tltmQuickSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(true);
			tltmAdvancedSearch.setSelection(false);
			tltmSort.setSelection(false);
			searcher = quickSearchPanel;				
			stack.topControl = quickSearchPanel;
			contentPanel.layout();
			if (lastSearch != quickSearchPanel && (quickSearchPanel.getQuery().length() > 0 || advancedSearchPanel.getQuery().length() > 0))
				refresh();
			lastSearch = searcher;
		});
		
		tltmAdvancedSearch.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(false);
			tltmAdvancedSearch.setSelection(true);
			tltmSort.setSelection(false);
			searcher = advancedSearchPanel;
			stack.topControl = advancedSearchPanel;
			advancedSearchPanel.clicked();
			contentPanel.layout();
			if (lastSearch != advancedSearchPanel && (quickSearchPanel.getQuery().length() > 0 || advancedSearchPanel.getQuery().length() > 0))
				refresh();
			lastSearch = searcher;
		});
		
		tltmSort.addListener(SWT.Selection, e -> {
			tltmQuickSearch.setSelection(false);
			tltmAdvancedSearch.setSelection(false);
			tltmSort.setSelection(true);
			stack.topControl = sorter;
			sorter.clicked();
			contentPanel.layout();
		});
	
		tltmQuickSearch.setSelection(true);
		searcher = quickSearchPanel;
		stack.topControl = quickSearchPanel;
		quickSearchPanel.ok();
	}
	
	public String getBaseExpression() {
		return baseExpression;
	}

	public Vector<Attribute> getAttributes() {
		return dbConnection.getAttributesOf(getBaseExpression());
	}

	public Vector<String> getAttributeNames() {
		Vector<String> names = new Vector<>();
		for (Attribute attribute: getAttributes())
			names.add(attribute.getName());
		return names;
	}
	
	public String getQuery() {
		return "(" + baseExpression + ")" + searcher.getQuery() + sorter.getQuery();
	}
	
	public void addUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.add(updateListener);
	}
	
	public void removeUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.remove(updateListener);
	}

	public void clearListeners() {
		updateListeners.clear();
	}
	
}
