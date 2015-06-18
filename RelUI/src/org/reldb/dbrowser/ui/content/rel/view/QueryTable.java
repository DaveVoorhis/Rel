package org.reldb.dbrowser.ui.content.rel.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Heading;

public class QueryTable extends DbTreeTab {
	
	public QueryTable(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		setControl(getContents(parent.getTabFolder()));
	}

	protected Tuples getTuples() {
		return relPanel.getConnection().getTuples(dbTreeItem.getName());
	}
	
	protected Composite getContents(Composite parent) {
		Table table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		Tuples tuples = getTuples();
		
		Heading heading = tuples.getHeading();
		for (Attribute attribute: heading.toArray()) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(attribute.getName());
			column.setToolTipText(attribute.getType().toString());
		}
		
		for (Tuple tuple: tuples) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int i=0; i<heading.getCardinality(); i++)
				item.setText(i, tuple.getAttributeValue(i).toString());
		}
		
		for (int i=0; i<table.getColumnCount(); i++)
			table.getColumn(i).pack();
		
		return table;
	}

}