package org.reldb.dbrowser.ui.content.rel.var;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Heading;
import org.reldb.rel.utilities.StringUtils;

public class EditTable extends DbTreeTab {
	
	Color changeColor = new Color(getDisplay(), 240, 240, 128);
	Color failColor = new Color(getDisplay(), 240, 128, 128);

	private HashSet<String> keyAttributeNames = new HashSet<String>();
	private HashSet<Integer> keyColumnNumbers = new HashSet<Integer>();
	private HashSet<Integer> stringColumnNumbers = new HashSet<Integer>();
	
	private HashMap<Integer, Vector<String>> originalKeyValues = new HashMap<Integer, Vector<String>>();
	private HashMap<Integer, HashSet<Integer>> changedColumnNumbers = new HashMap<Integer, HashSet<Integer>>();
	
	private Table table;
	private HashSet<Integer> rowNeedsProcessing = new HashSet<Integer>();
	private int focusCount = 0;
	private Timer updateTimer = new Timer();
	private int lastUpdatedRow = -1;
	private boolean readonly = false;

	public EditTable(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		obtainKeyDefinitions();
		setControl(getContents(parent.getTabFolder()));
	}

	private void obtainKeyDefinitions() {
		Tuples keyDefinitions = (Tuples)relPanel.getConnection().evaluate("((sys.Catalog WHERE Name = '" + dbTreeItem.getName() + "') {Keys}) UNGROUP Keys");
		for (Tuple keyDefinition: keyDefinitions) {
			Tuples keyAttributes = (Tuples)(keyDefinition.get("Attributes"));
			for (Tuple keyAttribute: keyAttributes) {
				String name = keyAttribute.get("Name").toString();
				keyAttributeNames.add(name);
			}
			return;
		}
		readonly = true;
	}
	
	private Tuples obtainTuples() {
		return relPanel.getConnection().getTuples(dbTreeItem.getName());
	}
	
	private void updateRow(TableItem updateRow, int rownum) {
		String keyspec = "";
		int index = 0;
		for (Integer keyColumn: keyColumnNumbers) {
			Vector<String> keyValues = originalKeyValues.get(rownum);
			if (keyspec.length() > 0)
				keyspec += " AND ";
			String preparedAttribute = keyValues.get(index);
			if (stringColumnNumbers.contains(keyColumn))
				preparedAttribute = "'" + StringUtils.quote(preparedAttribute) + "'";
			keyspec += table.getColumn(keyColumn).getText() + " = " + preparedAttribute;
			index++;
		}
		
		String updateQuery = "UPDATE " + dbTreeItem.getName() + " WHERE " + keyspec + ": {";
		String updateAttributes = "";
		HashSet<Integer> changedColumns = changedColumnNumbers.get(rownum);
		for (Integer changedColumn: changedColumns) {
			if (updateAttributes.length() > 0)
				updateAttributes += ", ";
			String preparedAttribute = table.getItem(rownum).getText(changedColumn);
			if (stringColumnNumbers.contains(changedColumn))
				preparedAttribute = "'" + StringUtils.quote(preparedAttribute) + "'";
			updateAttributes += table.getColumn(changedColumn).getText() + " := " + preparedAttribute;
		}
		updateQuery += updateAttributes + "};";

		System.out.println("EditTable: query is " + updateQuery);
		
		boolean updateSucceeds = relPanel.getConnection().execute(updateQuery);
		
		if (updateSucceeds) {
			for (int c = 1; c < table.getColumnCount(); c++)
				updateRow.setBackground(c, null);
			updateRow.setText(0, " ");
			originalKeyValues.remove(rownum);
			changedColumnNumbers.remove(rownum);
		} else {
			for (int c = 1; c < table.getColumnCount(); c++)
				updateRow.setBackground(c, failColor);
			updateRow.setText(0, "!");	
		}		
	}

	private void appendNewTuple() {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, "+");
		for (int i = 0; i < table.getColumnCount() - 1; i++)
			item.setText(i + 1, "");		
	}
	
	private void addRow(TableItem addRow, int rownum) {		
		String insertQuery = "INSERT " + dbTreeItem.getName() + " RELATION {TUPLE {";
		String insertAttributes = "";
		for (int column = 1; column < table.getColumnCount(); column++) {
			if (insertAttributes.length() > 0)
				insertAttributes += ", ";
			String preparedAttribute = table.getItem(rownum).getText(column);
			if (stringColumnNumbers.contains(column))
				preparedAttribute = "'" + StringUtils.quote(preparedAttribute) + "'";
			insertAttributes += table.getColumn(column).getText() + " " + preparedAttribute;
		}
		insertQuery += insertAttributes + "}};";

		System.out.println("EditTable: query is " + insertQuery);
		
		boolean updateSucceeds = relPanel.getConnection().execute(insertQuery);

		if (updateSucceeds) {
			for (int c = 1; c < table.getColumnCount(); c++)
				addRow.setBackground(c, null);
			addRow.setText(0, " ");
			originalKeyValues.remove(rownum);
			changedColumnNumbers.remove(rownum);			
			appendNewTuple();
		} else {
			for (int c = 1; c < table.getColumnCount(); c++)
				addRow.setBackground(c, failColor);
			addRow.setText(0, "*");	
		}
	}
	
	private synchronized void processRows() {
		for (Integer rownum: rowNeedsProcessing) {			
			TableItem processRow = table.getItem(rownum);
			if (processRow.getText(0).equals("+") || processRow.getText(0).equals("*"))
				addRow(processRow, rownum);
			else
				updateRow(processRow, rownum);
		}
		rowNeedsProcessing.clear();
	}
	
	private void updateTimerReset() {
		updateTimer.cancel();
		updateTimer = new Timer();
		updateTimer.schedule(new TimerTask() {
			public void run() {
				if (focusCount == 0) {
					if (isDisposed())
						return;
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (isDisposed())
								return;
							processRows();
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

		Tuples tuples = obtainTuples();

		originalKeyValues.clear();
		
		TableColumn rowMarker = new TableColumn(table, SWT.NONE);
		rowMarker.setText(" ");
		
		changedColumnNumbers.clear();
		keyColumnNumbers.clear();
		stringColumnNumbers.clear();
		int columnNumber = 1;
		Heading heading = tuples.getHeading();
		for (Attribute attribute : heading.toArray()) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			String columnHeading = attribute.getName();
			String columnType = attribute.getType().toString();
			if (columnType.equalsIgnoreCase("CHARACTER") || columnType.equalsIgnoreCase("CHAR"))
				stringColumnNumbers.add(columnNumber);
			if (keyAttributeNames.contains(columnHeading)) {
				column.setImage(IconLoader.loadIconSmall("bullet_key"));
				keyColumnNumbers.add(columnNumber);
			}
			column.setText(columnHeading);
			column.setToolTipText(attribute.getType().toString());
			columnNumber++;
		}

		// relvar tuples
		for (Tuple tuple : tuples) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(" ");
			for (int i = 0; i < heading.getCardinality(); i++)
				item.setText(i + 1, tuple.getAttributeValue(i).toString());
		}

		// blank "new" tuple
		appendNewTuple();
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumn(i).pack();

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		Listener mouseListener = new Listener() {
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
												processRows();
												lastUpdatedRow = -1;
											}
											String editedText = text.getText();
											if (!item.getText(column).equals(editedText)) {
												item.setBackground(column, changeColor);
												rowNeedsProcessing.add(rownum);
												lastUpdatedRow = rownum;
												if (!originalKeyValues.containsKey(rownum)) {
													Vector<String> keyColumnValues = new Vector<String>();
													for (Integer columnNumber: keyColumnNumbers)
														keyColumnValues.add(item.getText(columnNumber));
													originalKeyValues.put(rownum, keyColumnValues);
												}
												item.setText(column, editedText);
												HashSet<Integer> changedColumns = changedColumnNumbers.get(rownum);
												if (changedColumns == null) {
													changedColumns = new HashSet<Integer>();
													changedColumnNumbers.put(rownum, changedColumns);
												}
												changedColumns.add(column);
												if (item.getText(0).equals("+") || item.getText(0).equals("*"))
													item.setText(0, "*");
												else
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
		
		Listener keyListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.character == '\u0008' || event.character == '\u007F') {
					System.out.println("EditTable: delete selected");
				}
			}
		};
		
		if (!readonly) {
			table.addListener(SWT.MouseDown, mouseListener);
			table.addListener(SWT.KeyUp, keyListener);
		}

		return table;
	}

}