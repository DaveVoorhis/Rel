package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class FilterSorter extends Composite {

	@FunctionalInterface
	public interface FilterSorterUpdate {
		public void update(FilterSorter originator);
	}
	
	private Text clause;
	private String expression;
	private Vector<FilterSorterUpdate> updateListeners = new Vector<>();
	
	private void fireUpdate() {
		for (FilterSorterUpdate listener: updateListeners)
			listener.update(this);
	}
	
	public FilterSorter(Composite parent, int style, String baseExpression, FilterSorterState initialState) {
		super(parent, style);
		this.expression = baseExpression;

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginLeft = 5;
		setLayout(gridLayout);
		
		clause = new Text(this, SWT.BORDER);
		clause.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		if (initialState != null)
			clause.setText(initialState.getRepresentation());
		
		Button activate = new Button(this, SWT.NONE);
		activate.setText("Go");
		activate.addListener(SWT.Selection, e -> fireUpdate());		
	}
	
	public FilterSorter(Composite parent, int style, String baseExpression) {
		this(parent, style, baseExpression, null);
	}

	public String getExpression() {
		return expression;
	}
	
	public String getQuery() {
		return "(" + expression + ")" + ((clause.getText().trim().length() > 0) ? " WHERE " + clause.getText() : "");
	}
	
	public void addUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.add(updateListener);
	}
	
	public void removeUpdateListener(FilterSorterUpdate updateListener) {
		updateListeners.remove(updateListener);
	}

	public FilterSorterState getState() {
		return new FilterSorterState(clause.getText());
	}

}
