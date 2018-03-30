package org.reldb.dbrowser.ui.content.filtersorter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.IconLoader;

public class SortOrderPicker extends Composite {
	
	private String state = "";
	
	private Button ascending;
	private Button descending;
	private Button cancel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SortOrderPicker(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, true));
		
		ascending = new Button(this, SWT.TOGGLE);
		ascending.setImage(IconLoader.loadIcon("sort-ascending"));
		ascending.addListener(SWT.Selection, e -> setState("ASC"));
		ascending.setToolTipText("ASC - ascending");
		
		descending = new Button(this, SWT.TOGGLE);
		descending.setImage(IconLoader.loadIcon("sort-descending"));
		descending.addListener(SWT.Selection, e -> setState("DESC"));
		descending.setToolTipText("DESC - descending");
		
		cancel = new Button(this, SWT.PUSH);
		cancel.setImage(IconLoader.loadIcon("cancel"));		
		cancel.addListener(SWT.Selection, e -> setState(""));
		cancel.setToolTipText("Unsorted");
		
		setState("");	
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		switch (state) {
		case "ASC":
			this.state = state;
			ascending.setSelection(true);
			descending.setSelection(false);
			cancel.setVisible(true);
			break;
		case "DESC":
			this.state = state;
			ascending.setSelection(false);
			descending.setSelection(true);
			cancel.setVisible(true);
			break;
		case "":
			this.state = state;
			ascending.setSelection(false);
			descending.setSelection(false);
			cancel.setVisible(false);
			break;
		}
	}
	
}
