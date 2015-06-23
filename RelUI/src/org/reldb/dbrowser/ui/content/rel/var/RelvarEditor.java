package org.reldb.dbrowser.ui.content.rel.var;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Heading;
import org.reldb.rel.utilities.StringUtils;

public class RelvarEditor {
	
	private static boolean askDeleteConfirm = true;
	
	private Color changeColor;
	private Color failColor;

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
	
	private Composite parent;
	private DbConnection connection;
	private String relvarName;

	public RelvarEditor(Composite parent, DbConnection connection, String relvarName) {
		changeColor = new Color(parent.getDisplay(), 240, 240, 128);
		failColor = new Color(parent.getDisplay(), 240, 128, 128);
		this.parent = parent;
		this.connection = connection;
		this.relvarName = relvarName;

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		table.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				focusCount--;
				scheduleForUpdate();
			}
			public void focusGained(FocusEvent e) {
				focusCount++;
			}
		});

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		
		Listener mouseListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int row = table.getTopIndex();
				// get around apparent bug selecting first row if table.getItemCount() > 1
				if (table.getItemCount() > 1)
					row--;
				while (row < table.getItemCount()) {
					final int rownum = row;
					boolean visible = false;
					final TableItem item = table.getItem(row);
					for (int col = 1; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							final int column = col;
							if (row == table.getItemCount() - 1)
								return;
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
											scheduleForUpdate();
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
					askDeleteSelected();
				} else if (event.keyCode == SWT.F5) {
					refresh();
				}
			}
		};
		
		SelectionAdapter selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				if (table.getSelectionIndex() != lastUpdatedRow && lastUpdatedRow != -1) {
					processRows();
					lastUpdatedRow = -1;
				}
			}
		};
		
		if (!readonly) {
			table.addSelectionListener(selectionListener);
			table.addListener(SWT.MouseDown, mouseListener);
			table.addListener(SWT.KeyUp, keyListener);
		}
		
		refresh();
	}
	
	public Control getControl() {
		return table;
	}
	
	public void refresh() {		
		obtainKeyDefinitions();
		
		while (table.getColumnCount() > 0)
			table.getColumn(0).dispose();
		table.removeAll();
		
		Tuples tuples = obtainTuples();

		originalKeyValues.clear();		
		rowNeedsProcessing.clear();
		changedColumnNumbers.clear();
		keyColumnNumbers.clear();
		stringColumnNumbers.clear();

		// marker column
		(new TableColumn(table, SWT.NONE)).setText(" ");
		// column headings
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
		for (Tuple tuple: tuples) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(" ");
			for (int i = 0; i < heading.getCardinality(); i++)
				item.setText(i + 1, tuple.getAttributeValue(i).toString());
		}

		// blank "new" tuple
		appendNewTuple();
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumn(i).pack();
	}
	
	public void doDeleteSelected() {
		String selection = "";
		TableItem[] selectedRows = table.getSelection();
		if (selectedRows.length == 0)
			return;
		for (TableItem row: selectedRows) {
			if (row.getText(0) != " ")
				continue;
			if (selection.length() > 0)
				selection += " OR ";
			selection += "(" + getKeySelectionExpression(row) + ")";
		}
		String query = "DELETE " + relvarName + " WHERE " + selection + ";";
		
		System.out.println("EditTable: query is " + query);
		
		DbConnection.ExecuteResult result = connection.execute(query);
		
		if (result.failed())
			showError(query, "Unable to delete tuples.\n\nQuery: " + query + " failed:\n\n" + result.getErrorMessage());
		else
			refresh();
	}

	public int getTupleSelectionCount() {
		TableItem[] selectedRows = table.getSelection();
		if (selectedRows.length == 0)
			return 0;
		int count = 0;
		for (TableItem row: selectedRows) {
			if (row.getText(0) != " ")
				continue;
			count++;
		}		
		return count;
	}
	
	public void askDeleteSelected() {
		if (askDeleteConfirm) {
			int tupleSelectionCount = getTupleSelectionCount();
			if (tupleSelectionCount == 0)
				return;
			DeleteConfirmDialog confirmer = new DeleteConfirmDialog(parent.getShell(), tupleSelectionCount) {
				public void buttonPressed() {
					askDeleteConfirm = getAskDeleteConfirm();
				}
			};
			if (confirmer.open() != DeleteConfirmDialog.OK)
				return;
		}
		doDeleteSelected();
	}

	public void goToInsertRow() {
		if (table.getItemCount() < 0)
			return;
		TableItem insertionPoint = table.getItem(table.getItemCount() - 1);
		table.showItem(insertionPoint);
		table.setSelection(insertionPoint);
	}

	private void obtainKeyDefinitions() {
		readonly = false;
		keyAttributeNames.clear();
		Tuples keyDefinitions = (Tuples)connection.evaluate("((sys.Catalog WHERE Name = '" + relvarName + "') {Keys}) UNGROUP Keys");
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
		return connection.getTuples(relvarName);
	}
	
	private String getKeySelectionExpression(Vector<String> keyValues) {
		String keyspec = "";
		int index = 0;
		for (Integer keyColumn: keyColumnNumbers) {
			if (keyspec.length() > 0)
				keyspec += " AND ";
			String preparedAttribute = keyValues.get(index);
			if (stringColumnNumbers.contains(keyColumn))
				preparedAttribute = "'" + StringUtils.quote(preparedAttribute) + "'";
			keyspec += table.getColumn(keyColumn).getText() + " = " + preparedAttribute;
			index++;
		}
		return keyspec;
	}
	
	private Vector<String> getKeySelectionValuesForRow(TableItem row) {
		Vector<String> keyValues = new Vector<String>();
		for (Integer keyColumn: keyColumnNumbers) {
			String currentValue = row.getText(keyColumn);
			keyValues.add(currentValue);
		}
		return keyValues;
	}
	
	private String getKeySelectionExpression(TableItem row) {
		return getKeySelectionExpression(getKeySelectionValuesForRow(row));
	}
	
	private void updateRow(TableItem updateRow, int rownum) {
		String keyspec = getKeySelectionExpression(originalKeyValues.get(rownum));
		
		String updateQuery = "UPDATE " + relvarName + " WHERE " + keyspec + ": {";
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
		
		DbConnection.ExecuteResult result = connection.execute(updateQuery);
		
		if (!result.failed()) {
			for (int c = 1; c < table.getColumnCount(); c++)
				updateRow.setBackground(c, null);
			updateRow.setText(0, " ");
			originalKeyValues.remove(rownum);
			changedColumnNumbers.remove(rownum);
		} else {
			for (int c = 1; c < table.getColumnCount(); c++)
				updateRow.setBackground(c, failColor);
			updateRow.setText(0, "!");	
			showError(updateQuery, "Unable to update tuples.\n\nQuery: " + updateQuery + " failed:\n\n" + result.getErrorMessage());			
		}
	}

	private void appendNewTuple() {
		TableItem item = table.getItem(table.getItemCount() - 1);
		item.setText(0, "+");
		for (int i = 0; i < table.getColumnCount() - 1; i++)
			item.setText(i + 1, "");
		item = new TableItem(table, SWT.NONE);
		item.setText(0, "");
		for (int i = 0; i < table.getColumnCount() - 1; i++)
			item.setText(i + 1, "");		
	}
	
	private void addRow(TableItem addRow, int rownum) {		
		String insertQuery = "D_INSERT " + relvarName + " RELATION {TUPLE {";
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
		
		DbConnection.ExecuteResult result = connection.execute(insertQuery);

		if (!result.failed()) {
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
			showError(insertQuery, "Unable to insert tuples.\n\nQuery: " + insertQuery + " failed:\n\n" + result.getErrorMessage());			
		}
	}
	
	private boolean errorDisplayed = false;
	
	private void showError(String query, String msg) {
		errorDisplayed = true;
		MessageDialog.openError(parent.getShell(), "Error", msg);
		errorDisplayed = false;
	}
	
	private synchronized void processRows() {
		if (errorDisplayed)
			return;
		for (Integer rownum: rowNeedsProcessing) {			
			TableItem processRow = table.getItem(rownum);
			if (processRow.getText(0).equals("+") || processRow.getText(0).equals("*"))
				addRow(processRow, rownum);
			else
				updateRow(processRow, rownum);
		}
		rowNeedsProcessing.clear();
	}
	
	private void scheduleForUpdate() {
		updateTimer.cancel();
		updateTimer = new Timer();
		updateTimer.schedule(new TimerTask() {
			public void run() {
				if (focusCount == 0) {
					if (parent.isDisposed())
						return;
					parent.getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (parent.isDisposed())
								return;
							processRows();
						}
					});
				}
			}
		}, 500);
	}

}