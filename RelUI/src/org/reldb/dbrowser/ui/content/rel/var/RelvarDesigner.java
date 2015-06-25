package org.reldb.dbrowser.ui.content.rel.var;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
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
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelvarDesigner {
	
	private Table table;	
	private DbConnection connection;
	private String relvarName;

	public RelvarDesigner(Composite parent, DbConnection connection, String relvarName) {
		this.connection = connection;
		this.relvarName = relvarName;

		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);	

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
							switch (col) {
							case 1:	// name
								final Text text = new Text(table, SWT.NONE);
								Listener textListener = new Listener() {
									@Override
									public void handleEvent(final Event e) {
										switch (e.type) {
										case SWT.FocusOut:
											e.detail = SWT.TRAVERSE_RETURN;
											// FALL THROUGH
										case SWT.Traverse:
											switch (e.detail) {
											case SWT.TRAVERSE_RETURN:
												String editedText = text.getText();
												if (!item.getText(column).equals(editedText)) {
													item.setText(column, editedText);
													if (item.getText(0).equals("+") || item.getText(0).equals("*"))
														item.setText(0, "*");
													else
														item.setText(0, ">");
												}
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
								editor.setEditor(text, item, column);
								text.setText(item.getText(column));
								text.setFocus();
								break;
							case 2: // type
								final Combo type = new Combo(table, SWT.NONE);
								populateTypeCombo(type);
								Listener typeListener = new Listener() {
									@Override
									public void handleEvent(final Event e) {
										switch (e.type) {
										case SWT.FocusOut:
											e.detail = SWT.TRAVERSE_RETURN;
											// FALL THROUGH
										case SWT.Traverse:
											switch (e.detail) {
											case SWT.TRAVERSE_RETURN:
												String editedText = type.getText();
												if (!item.getText(column).equals(editedText)) {
													item.setText(column, editedText);
													if (item.getText(0).equals("+") || item.getText(0).equals("*"))
														item.setText(0, "*");
													else
														item.setText(0, ">");
												}
												// FALL THROUGH
											case SWT.TRAVERSE_ESCAPE:
												type.dispose();
												e.doit = false;
											}
											break;
										}
									}
								};
								type.addListener(SWT.FocusOut, typeListener);
								type.addListener(SWT.Traverse, typeListener);
								editor.setEditor(type, item, column);
								type.setText(item.getText(column));
								type.setFocus();
								break;								
							default:
								/*
								final Composite keySelectImage = new Composite(table, SWT.NONE);
								keySelectImage.setBackgroundImage(IconLoader.loadIconSmall("bullet_key"));
								Vector<String> keyDef = keyDefs.get(column - 1);
								keySelectImage.setVisible(keyDef.contains(item.getText(column)));
								*/
								break;
							}
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
					deleteRow();
				} else if (event.keyCode == SWT.F5) {
					refresh();
				}
			}
		};
		
		table.addListener(SWT.MouseDown, mouseListener);
		table.addListener(SWT.KeyUp, keyListener);
		
		refresh();
	}

	public Control getControl() {
		return table;
	}
	
	private Vector<Vector<String>> keyDefs = null;
	
	public void refresh() {		
		while (table.getColumnCount() > 0)
			table.getColumn(0).dispose();
		table.removeAll();
		
		keyDefs = obtainKeys();
		
		// marker column
		(new TableColumn(table, SWT.NONE)).setText(" ");
		
		// name
		(new TableColumn(table, SWT.NONE)).setText("Name");

		// type
		(new TableColumn(table, SWT.NONE)).setText("Type");
		
		// key (existing definitions)
		for (int i=0; i<keyDefs.size(); i++)
			(new TableColumn(table, SWT.NONE)).setImage(IconLoader.loadIconSmall("bullet_key"));
		
		// key (new definition)
		(new TableColumn(table, SWT.NONE)).setImage(IconLoader.loadIconSmall("bullet_key"));
		
		// relvar tuples
		Tuples tuples = obtainAttributes();
		for (Tuple tuple: tuples) {
			TableItem row = new TableItem(table, SWT.NONE);
			String attributeName = tuple.get("Name").toString();
			row.setText(1, attributeName);
			row.setText(2, tuple.get("TypeName").toString());
			int columnNumber = 3;
			for (Vector<String> keyDef: keyDefs) {
				for (String keyAttributeName: keyDef)
					if (attributeName.equals(keyAttributeName))
						row.setImage(columnNumber, IconLoader.loadIconSmall("bullet_key"));
				columnNumber++;
			}
		}

		// blank "new" tuple
		appendRow();
		
		for (int i = 0; i < table.getColumnCount(); i++)
			table.getColumn(i).pack();
	}

	private void deleteRow() {
		
	}
	
	private void appendRow() {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, "+");
		for (int i = 0; i < table.getColumnCount() - 1; i++)
			item.setText(i + 1, " ");		
	}
	
	protected void populateTypeCombo(Combo type) {
		Tuples typeNames = connection.getTuples("sys.Types {Name}");
		for (Tuple typeName: typeNames) 
			type.add(typeName.get("Name").toString());
	}
	
	private Tuples obtainAttributes() {
		return connection.getTuples("((sys.Catalog WHERE Name = '" + relvarName + "') {Attributes}) UNGROUP Attributes");
	}
	
	private Vector<Vector<String>> obtainKeys() {
		Vector<Vector<String>> keys = new Vector<Vector<String>>();
		Tuples keysTuples = connection.getTuples("((sys.Catalog WHERE Name='" + relvarName + "') {Keys}) UNGROUP Keys");
		for (Tuple key: keysTuples) {
			Vector<String> attributes = new Vector<String>();
			Tuples attributeTuples = (Tuples)key.get("Attributes");
			for (Tuple attribute: attributeTuples)
				attributes.add(attribute.get("Name").toString());
			keys.add(attributes);
		}
		return keys;
	}
	
}