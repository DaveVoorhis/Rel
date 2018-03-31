package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

public class FilterSorterState {

	private boolean quickSearchIsActive = true;
	private String quickSearchState;
	private Vector<String[]> advancedSearchState;
	private String advancedSearchManualOverride;
	boolean isAdvancedSearchManualOverride;
	private String sortSpec;
	
	public FilterSorterState() {
		quickSearchState = "";
		advancedSearchState = new Vector<>();
		advancedSearchManualOverride = "";
		isAdvancedSearchManualOverride = false;
		sortSpec = "";
	}
	
	public void setQuickSearchState(String quickSearchState) {
		this.quickSearchState = quickSearchState;
	}
	
	public String getQuickSearchState() {
		return quickSearchState;
	}

	public Vector<String[]> getAdvancedSearchState() {
		return advancedSearchState;
	}
	
	public boolean isQuickSearch() {
		return quickSearchIsActive;
	}

	public void setAdvancedSearchIsActive() {
		quickSearchIsActive = false;
	}

	public void setQuickSearchIsActive() {
		quickSearchIsActive = true;
	}

	public void setSortSpec(String sortSpec) {
		this.sortSpec = sortSpec;
	}

	public String getSortSpec() {
		return sortSpec;
	}

	public void setAdvancedSearchManualOverride(String search) {
		advancedSearchManualOverride = search;
	}
	
	public String getAdvancedSearchManualOverride() {
		return advancedSearchManualOverride;
	}

	public boolean isAdvancedSearchManualOverrideActive() {
		return isAdvancedSearchManualOverride;
	}

	public void setAdvancedSearchManualOverrideActive(boolean override) {
		isAdvancedSearchManualOverride = override;
	}
}
