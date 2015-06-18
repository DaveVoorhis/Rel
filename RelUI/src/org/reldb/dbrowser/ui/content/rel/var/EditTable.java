package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Heading;

public class EditTable extends DbTreeTab {

	public EditTable(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		setControl(getContents(parent.getTabFolder()));
	}

	protected Tuples getTuples() {
		return relPanel.getConnection().getTuples(dbTreeItem.getName());
	}

	protected Composite getContents(Composite parent) {
		Color changeColor = new Color(parent.getDisplay(), 240, 240, 128);
		
		Table table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		Tuples tuples = getTuples();

		TableColumn rowMarker = new TableColumn(table, SWT.NONE);
		rowMarker.setText(" ");
		
		Heading heading = tuples.getHeading();
		for (Attribute attribute : heading.toArray()) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(attribute.getName());
			column.setToolTipText(attribute.getType().toString());
		}

		// relvar tuples
		for (Tuple tuple : tuples) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(" ");
			for (int i = 0; i < heading.getCardinality(); i++)
				item.setText(i + 1, tuple.getAttributeValue(i).toString());
		}

		// blank "new" tuple
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, "+");
		for (int i = 0; i < heading.getCardinality(); i++)
			item.setText(i + 1, "");		
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumn(i).pack();

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		Listener editListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int row = table.getTopIndex();
				while (row < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(row);
					for (int col = 1; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							final int column = col;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								@Override
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							text.addModifyListener(new ModifyListener() {
								public void modifyText(ModifyEvent e) {
									if (!item.getText(column).equals(text.getText()))
										item.setBackground(column, changeColor);
								}								
							});
							editor.setEditor(text, item, column);
							text.setText(item.getText(column));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					row++;
				}
			}
		};
		
		table.addListener(SWT.MouseDown, editListener);

		return table;
	}

}