package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class NaiveShowTab extends DbTreeTab {
	
	private StyledText definition;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public NaiveShowTab(RelPanel parent, DbTreeItem item, String definition) {
		super(parent, item);		
		setControl(getContents(parent.getTabFolder()));
		this.definition.setText(definition);
	}

	protected Composite getContents(Composite parent) {
		definition = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		definition.setEditable(false);
		return definition;
	}

}
