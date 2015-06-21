package org.reldb.dbrowser.ui.content.rel.var;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
	
	Color changeColor = new Color(getDisplay(), 240, 240, 128);
	Color failColor = new Color(getDisplay(), 240, 128, 128);

	private Table table;	
	private HashSet<Integer> rowNeedsUpdate = new HashSet<Integer>();
	private int focusCount = 0;
	private Timer updateTimer = new Timer();
	private int lastUpdatedRow = -1;

	public EditTable(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		setControl(getContents(parent.getTabFolder()));
	}

	protected Tuples getTuples() {
		return relPanel.getConnection().getTuples(dbTreeItem.getName());
	}
	
	private synchronized void updateRows() {
		for (Integer rownum: rowNeedsUpdate) {
			TableItem updateRow = table.getItem(rownum);
			
			boolean updateSucceeds = Math.random() > 0.75;
			
			if (updateSucceeds) {
				for (int c = 1; c < table.getColumnCount(); c++)
					updateRow.setBackground(c, null);
				updateRow.setText(0, " ");
			} else {
				for (int c = 1; c < table.getColumnCount(); c++)
					updateRow.setBackground(c, failColor);
				updateRow.setText(0, "!");				
			}
		}
		rowNeedsUpdate.clear();		
	}
	
	private void updateTimerReset() {
		updateTimer.cancel();
		updateTimer = new Timer();
		updateTimer.schedule(new TimerTask() {
			public void run() {
				if (focusCount == 0) {
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							updateRows();
						}
					});
				}
			}
		}, 500);
	}
	
	protected Composite getContents(Composite parent) {
		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		table.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				focusCount--;
				updateTimerReset();
			}
			public void focusGained(FocusEvent e) {
				focusCount++;
			}
		});

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
				int row = table.getTopIndex() - 1;
				while (row < table.getItemCount()) {
					final int rownum = row;
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
									case SWT.FocusIn:
										focusCount++;
										break;
									case SWT.FocusOut:
										e.detail = SWT.TRAVERSE_RETURN;
										// FALL THROUGH
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											if (lastUpdatedRow >= 0 && lastUpdatedRow != rownum) {
												updateRows();
												lastUpdatedRow = -1;
											}
											if (!item.getText(column).equals(text.getText())) {
												item.setBackground(column, changeColor);
												rowNeedsUpdate.add(rownum);
												lastUpdatedRow = rownum;
												item.setText(column, text.getText());
												item.setText(0, ">");
											}
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											focusCount--;
											text.dispose();
											e.doit = false;
											updateTimerReset();
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusIn, textListener);
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
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