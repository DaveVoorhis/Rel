package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.CellEditorCreatedEvent;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.MultiLineTextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.gui.ICellEditDialog;
import org.eclipse.nebula.widgets.nattable.export.ExportConfigAttributes;
import org.eclipse.nebula.widgets.nattable.export.command.ExportCommand;
import org.eclipse.nebula.widgets.nattable.extension.poi.HSSFExcelExporter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.ITraversalStrategy;
import org.eclipse.nebula.widgets.nattable.selection.MoveCellSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tickupdate.TickUpdateConfigAttributes;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.utilities.StringUtils;

public abstract class Editor extends Grid {
	
	private NatTable table;
	
	protected Attribute[] heading;
	protected Tuples tuples;
	protected DataProvider dataProvider;
	
	private HeadingProvider headingProvider;
	private DefaultGridLayer gridLayer;
	
	private boolean popupEdit = false;
	
	private int lastRowSelected = -1;
	
	enum RowAction {UPDATE, INSERT};
	
    class HeadingProvider implements IDataProvider {	    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			Attribute attribute = heading[columnIndex];
			switch (rowIndex) {
			case 0: return attribute.getName();
			case 1: return attribute.getType().toString();
			default: return "";
			}
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return heading.length;
		}

		@Override
		public int getRowCount() {
			return 2 + ((keys == null) ? 0 : (((keys.size() > 1) ? keys.size() - 1 : 0)));
		}
    };
	
	class Row {
		private HashMap<Integer, Object> originalData;
		private HashMap<Integer, Object> newData;
		private String error;
		private RowAction action;
		
		Row(Tuple tuple) {
			originalData = new HashMap<Integer, Object>();
			newData = new HashMap<Integer, Object>();
			for (int column=0; column<tuple.getAttributeCount(); column++)
				originalData.put(column, tuple.get(column));
			reset();
			action = RowAction.UPDATE;
		}
		
		Row() {
			originalData = new HashMap<Integer, Object>();
			newData = new HashMap<Integer, Object>();
			reset();
			action = RowAction.INSERT;
		}
		
		Object getOriginalColumnValue(int column) {
			return originalData.get(column);
		}
		
		Object getColumnValue(int column) {
			Object v = newData.get(column);
			if (v != null)
				return v;
			return getOriginalColumnValue(column);
		}
		
		void setColumnValue(int column, Object newValue) {
			newData.put(column, newValue);
		}

		boolean isChanged(int columnIndex) {
			return newData.containsKey(columnIndex);
		}
		
		private void reset() {
			newData.clear();
			error = null;
		}
		
		// Copy new data to original data, and clear new data
		void committed() {
			for (Entry<Integer, Object> entry: newData.entrySet())
				originalData.put(entry.getKey(), entry.getValue());
			reset();
			action = RowAction.UPDATE;
		}

		// Clear new data
		public void cancelled() {
			reset();
		}
		
		String getError() {
			return error;
		}
		
		void setError(String error) {
			this.error = error;
		}
		
		RowAction getAction() {
			return action;
		}
	}

    class DataProvider implements IDataProvider {
    	
    	private HashSet<Integer> processRows = new HashSet<Integer>();
    	private Vector<Row> rows = new Vector<Row>();
    	private String headingString;
    	
    	public DataProvider() {
    		reload();
    		headingString = getRelHeading();
    	}

		public void reload() {
			rows.clear();
    		Iterator<Tuple> iterator = tuples.iterator();
    		while (iterator.hasNext())
    			rows.add(new Row(iterator.next()));
    		rows.add(new Row());			
			processRows.clear();			
		}

		public String getError(int row) {
			if (row >= rows.size())
				return null;
			return rows.get(row).getError();
		}

		public boolean isChanged(int columnIndex, int rowIndex) {
			return rows.get(rowIndex).isChanged(columnIndex);
		}
    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return rows.get(rowIndex).getColumnValue(columnIndex);
		}

		private int getCountOfInsertErrors() {
			int count = 0;
			for (int row: processRows)
				if (rows.get(row).getError() != null && rows.get(row).getAction() == RowAction.INSERT)
					count++;
			return count;
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			if (newValue != null && newValue.toString().length() == 0)
				newValue = null;
			if (getDataValue(columnIndex, rowIndex) == null && newValue == null)
				return;
			if (getDataValue(columnIndex, rowIndex) != null && newValue != null)
				if (newValue.toString().equals(getDataValue(columnIndex, rowIndex).toString()))
					return;
			if (newValue == null)
				rows.get(rowIndex).setColumnValue(columnIndex, newValue);
			else
				rows.get(rowIndex).setColumnValue(columnIndex, newValue.toString());
			processRows.add(rowIndex);
			int lastRowIndex = rows.size() - 1;
			if (rowIndex == lastRowIndex && getCountOfInsertErrors() == 0) {
				rows.add(new Row());
				table.redraw();
			}
		}

		@Override
		public int getColumnCount() {
			return heading.length;
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}
				
		private String getKeySelectionExpression(int rownum) {
			HashSet<String> key;
			if (keys.size() == 0) {
				key = new HashSet<String>();
				for (int column = 0; column < heading.length; column++)
					key.add(heading[column].getName());
			}
			else
				key = keys.get(0);
			Row originalValues = rows.get(rownum);
			String keyspec = "";
			for (int column = 0; column < heading.length; column++) {
				String attributeName = heading[column].getName();
				if (key.contains(attributeName)) {
					if (keyspec.length() > 0)
						keyspec += " AND ";
					String attributeType = heading[column].getType().toString();
					Object attributeValueRaw = originalValues.getOriginalColumnValue(column);
					String attributeValue = "";
					if (attributeValueRaw != null)
						attributeValue = attributeValueRaw.toString();
					if (attributeType.equals("CHARACTER"))
						attributeValue = "'" + StringUtils.quote(attributeValue) + "'";
					keyspec += attributeName + " = " + attributeValue;
				}
			}
			return keyspec;
		}
		
		private void refreshAfterUpdate() {
			table.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!table.isDisposed())
						table.redraw();
				}
			});
		}
		
		private synchronized void updateRow(Row row, int rownum) {
			if (relvarName == null) {
				row.committed();
				processRows.remove(rownum);
				// TODO delete rows
			} else {
				String keyspec = getKeySelectionExpression(rownum);
				String updateQuery = "UPDATE " + relvarName + " WHERE " + keyspec + ": {";
				String updateAttributes = "";
				for (int column = 0; column < heading.length; column++) {
					if (row.isChanged(column)) {
						if (updateAttributes.length() > 0)
							updateAttributes += ", ";
						String attributeType = heading[column].getType().toString();
						String attributeValue = row.getColumnValue(column).toString();
						if (attributeType.equals("CHARACTER"))
							attributeValue = "'" + StringUtils.quote(attributeValue) + "'";
						String attributeName = heading[column].getName();
						updateAttributes += attributeName + " := " + attributeValue;
					}
				}
				updateQuery += updateAttributes + "};";
	
				System.out.println("RelvarEditor: query is " + updateQuery);
				
				DbConnection.ExecuteResult result = connection.execute(updateQuery);
				
				if (result.failed())
					row.setError("Unable to update tuples.\n\nQuery: " + updateQuery + " failed:\n\n" + result.getErrorMessage());
				else {
					row.committed();
					processRows.remove(rownum);
				}
			}
			
			refreshAfterUpdate();
		}
		
		private String getTupleDefinitionFor(Row row) {
			String insertAttributes = "";
			for (int column = 0; column < heading.length; column++) {
				if (insertAttributes.length() > 0)
					insertAttributes += ", ";
				String attributeType = heading[column].getType().toString();
				Object attributeValueRaw = row.getColumnValue(column);
				String attributeValue = "";
				if (attributeValueRaw != null)
					attributeValue = attributeValueRaw.toString();
				else if (attributeType.equals("BOOLEAN"))
					attributeValue = "False";
				else if (attributeType.equals("RATIONAL"))
					attributeValue = "0.0";
				else if (attributeType.equals("INTEGER"))
					attributeValue = "0";
				row.setColumnValue(column, attributeValue);
				if (attributeType.equals("CHARACTER"))
					attributeValue = "'" + StringUtils.quote(attributeValue) + "'";
				String attributeName = heading[column].getName();
				insertAttributes += attributeName + " " + attributeValue;
			}
			return "TUPLE {" + insertAttributes + "}";
		}
		
		private synchronized void insertRow(Row row, int rownum) {		
			if (relvarName == null) {
				row.committed();
				processRows.remove(rownum);
				// TODO insert rows
			} else {
				String insertQuery = "D_INSERT " + relvarName + " RELATION {" + getTupleDefinitionFor(row) + "};";
	
				System.out.println("RelvarEditor: query is " + insertQuery);
				
				DbConnection.ExecuteResult result = connection.execute(insertQuery);
	
				if (result.failed()) 
					row.setError("Unable to insert tuple.\n\nQuery: " + insertQuery + " failed:\n\n" + result.getErrorMessage());
				else {
					row.committed();
					processRows.remove(rownum);
				}
			}
			
			refreshAfterUpdate();
		}

		public void deleteRows(Set<Range> selections) {
			if (relvarName == null) {
				// TODO delete rows
			} else {
				String deleteQuery = "DELETE " + relvarName + " WHERE ";
				String allKeysSpec = "";
				int tupleCount = 0;
				for (Range range: selections)
					for (int rownum = range.start; rownum < range.end; rownum++) {
						if (rows.get(rownum).getAction() != RowAction.INSERT) {
							String keyspec = getKeySelectionExpression(rownum);
							if (allKeysSpec.length() > 0)
								allKeysSpec += " OR ";
							allKeysSpec += "(" + keyspec + ")";
						}
						tupleCount++;
					}
				deleteQuery += allKeysSpec + ";";
				
				System.out.println("RelvarEditor: query is " + deleteQuery);
			
				DbConnection.ExecuteResult result = connection.execute(deleteQuery);
				
				if (result.failed())
					MessageDialog.openError(table.getShell(), "DELETE Error", "Unable to delete tuple" + ((tupleCount>1) ? "s" : "") + ".\n\nQuery: " + deleteQuery + " failed:\n\n" + result.getErrorMessage());
				else
					refresh();
			}
		}

		public void processDirtyRows() {
			for (Integer rownum: processRows.toArray(new Integer[0]))
				if (rownum != lastRowSelected) {
					Row row = rows.get(rownum);
					switch (row.getAction()) {
						case INSERT: insertRow(row, rownum); break;
						case UPDATE: updateRow(row, rownum); break;
					}
				}
		}
		
		public int countDirtyRows() {
			return processRows.size();
		}

		public boolean isRVA(int columnIndex) {
			String attributeType = heading[columnIndex].getType().toString();
			return attributeType.startsWith("RELATION ");
		}
		
		private String getRelHeading() {
			return new TypeInfo(connection).getHeadingDefinition("TYPE_OF(" + getAttributeSource() + ")");
		}
		
		public String getLiteral() {
			String body = "";
			for (Row row: rows)
				body += ((body.length() > 0) ? ",\n" : "") + "\t" + getTupleDefinitionFor(row);
			return headingString + " {" + body + "}";
		}
    };
	
	class HeaderConfiguration extends AbstractRegistryConfiguration {
		public void configureRegistry(IConfigRegistry configRegistry) {
			ImagePainter keyPainter = new ImagePainter(IconLoader.loadIconSmall("bullet_key")); 
			CellPainterDecorator decorator = new CellPainterDecorator(
					new TextPainter(), 
					CellEdgeEnum.RIGHT, 
					keyPainter);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_PAINTER,
	                new BeveledBorderDecorator(decorator),
	                DisplayMode.NORMAL,
	                "keycolumnintegrated");
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_PAINTER,
	                new BeveledBorderDecorator(keyPainter),
	                DisplayMode.NORMAL,
	                "keycolumnalone");
	        BorderStyle borderStyle = new BorderStyle();
	        borderStyle.setColor(GUIHelper.COLOR_GRAY);
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_PAINTER, 
					new LineBorderDecorator(new TextPainter(), borderStyle), 
					DisplayMode.NORMAL, 
					GridRegion.CORNER);
		}
	}
	
	class EditorConfiguration extends AbstractRegistryConfiguration {
	    @Override
	    public void configureRegistry(IConfigRegistry configRegistry) {
	    	// editable
	        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
	        // style for "changed" cells
	        Style changedStyle = new Style();
	        changedStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
	        changedStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
	        configRegistry.registerConfigAttribute(
	        		CellConfigAttributes.CELL_STYLE,
	        		changedStyle,
	        		DisplayMode.NORMAL,
	        		"changed");
	        configRegistry.registerConfigAttribute(
	        		CellConfigAttributes.CELL_STYLE,
	        		changedStyle,
	        		DisplayMode.SELECT,
	        		"changed");
	        // style for "error" cells
	        Style errorStyle = new Style();
	        errorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
	        errorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
	        configRegistry.registerConfigAttribute(
	        		CellConfigAttributes.CELL_STYLE,
	        		errorStyle,
	        		DisplayMode.NORMAL,
	        		"error");
	        configRegistry.registerConfigAttribute(
	        		CellConfigAttributes.CELL_STYLE,
	        		errorStyle,
	        		DisplayMode.SELECT,
	        		"error");    	        
	        // options for Excel export
	        configRegistry.registerConfigAttribute(ExportConfigAttributes.EXPORTER, new HSSFExcelExporter());    	        
	        // style for selected cells
	        Style selectStyle = new Style();
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_STYLE, 
					selectStyle, 
					DisplayMode.SELECT);
			// default text editor
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new TextCellEditor(true, true) {
	                	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
	                		editorBeenOpened(getRowIndex(), getColumnIndex());
	                		return super.activateCell(parent, originalCanonicalValue);
	                	}
	                	public void close() {
	                		editorBeenClosed(getRowIndex(), getColumnIndex());
	                		super.close();
	                	}
	                }, 
	                DisplayMode.NORMAL);
	        // open adjacent editor when we leave the current one during editing
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.OPEN_ADJACENT_EDITOR,
	                Boolean.TRUE,
	                DisplayMode.EDIT);
	        // for each column...
	        for (int column = 0; column < heading.length; column++) {
	        	Attribute attribute = heading[column];
	        	String columnLabel = "column" + column;
	        	String type = attribute.getType().toString();
	        	if (type.equalsIgnoreCase("INTEGER"))
					registerIntegerColumn(configRegistry, columnLabel);
				else if (type.equalsIgnoreCase("RATIONAL"))
					registerRationalColumn(configRegistry, columnLabel);
				else if (type.equalsIgnoreCase("CHARACTER"))
					registerMultiLineEditorColumn(configRegistry, columnLabel);
				else if (type.equalsIgnoreCase("BOOLEAN"))
					registerBooleanColumn(configRegistry, columnLabel);
				else if (type.startsWith("RELATION ")) {
					String defaultValue = type + " {}";
					registerRvaColumn(configRegistry, columnLabel, defaultValue);
				} else
					registerDefaultColumn(configRegistry, columnLabel);
	        }
	    }

		private void registerDefaultColumn(IConfigRegistry configRegistry, String columnLabel) {
	        Style cellStyle = new Style();
	        cellStyle.setAttributeValue(
	                CellStyleAttributes.HORIZONTAL_ALIGNMENT,
	                HorizontalAlignmentEnum.LEFT);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.NORMAL,
	                columnLabel);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.EDIT,
	                columnLabel);
	    }
	    
	    private void registerBooleanColumn(IConfigRegistry configRegistry, String columnLabel) {
	        // register a CheckBoxCellEditor
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new CheckBoxCellEditor() {
	                	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
	                		editorBeenOpened(getRowIndex(), getColumnIndex());
	                		return super.activateCell(parent, originalCanonicalValue);
	                	}
	                	public void close() {
	                		editorBeenClosed(getRowIndex(), getColumnIndex());
	                		super.close();
	                	}
	                },
	                DisplayMode.EDIT,
	                columnLabel);

	        // if you want to use the CheckBoxCellEditor, you should also consider
	        // using the corresponding CheckBoxPainter to show the content like a
	        // checkbox in your NatTable
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_PAINTER,
	                new CheckBoxPainter(),
	                DisplayMode.NORMAL,
	                columnLabel);

	        // using a CheckBoxCellEditor also needs a Boolean conversion to work
	        // correctly
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.DISPLAY_CONVERTER,
	                new DefaultDisplayConverter() {
	                    @Override
	                    public Object canonicalToDisplayValue(Object canonicalValue) {
	                    	if (canonicalValue == null)
	                    		return null;
	                    	boolean isTrue = canonicalValue.toString().equalsIgnoreCase("True");
	                    	return new Boolean(isTrue);
	                    }
	                    @Override
	                    public Object displayToCanonicalValue(Object destinationValue) {
	                    	return ((Boolean)destinationValue).booleanValue() ? "True" : "False";
	                    }
	                },
	                DisplayMode.NORMAL,
	                columnLabel);
	    }

	    private void registerRationalColumn(IConfigRegistry configRegistry, String columnLabel) {
	        // configure the tick update dialog to use the adjust mode
	        configRegistry.registerConfigAttribute(
	                TickUpdateConfigAttributes.USE_ADJUST_BY,
	                Boolean.TRUE,
	                DisplayMode.EDIT,
	                columnLabel);
	        // Use Double converter
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.DISPLAY_CONVERTER,
	                new DefaultDoubleDisplayConverter(),
	                DisplayMode.NORMAL,
	                columnLabel);
	    }

	    private void registerIntegerColumn(IConfigRegistry configRegistry, String columnLabel) {
	        Style cellStyle = new Style();
	        cellStyle.setAttributeValue(
	                CellStyleAttributes.HORIZONTAL_ALIGNMENT,
	                HorizontalAlignmentEnum.RIGHT);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.NORMAL,
	                columnLabel);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.EDIT,
	                columnLabel);
	        // Use Integer converter
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.DISPLAY_CONVERTER,
	                new DefaultIntegerDisplayConverter(),
	                DisplayMode.NORMAL,
	                columnLabel);
	    }
	    
	    private void registerMultiLineEditorColumn(IConfigRegistry configRegistry, String columnLabel) {
	        // configure the multi line text editor
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new MultiLineTextCellEditor(false) {
	                	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
	                		editorBeenOpened(getRowIndex(), getColumnIndex());
	                		return super.activateCell(parent, originalCanonicalValue);
	                	}
	                	public void close() {
	                		editorBeenClosed(getRowIndex(), getColumnIndex());
	                		super.close();
	                	}
	                },
	                DisplayMode.NORMAL,
	                columnLabel);

	        Style cellStyle = new Style();
	        cellStyle.setAttributeValue(
	                CellStyleAttributes.HORIZONTAL_ALIGNMENT,
	                HorizontalAlignmentEnum.LEFT);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.NORMAL,
	                columnLabel);
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_STYLE,
	                cellStyle,
	                DisplayMode.EDIT,
	                columnLabel);

	        // configure custom dialog settings
	        Display display = Display.getCurrent();
	        Map<String, Object> editDialogSettings = new HashMap<String, Object>();
	        editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_TITLE, "Edit");
	        editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_ICON, display.getSystemImage(SWT.ICON_WARNING));
	        editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_RESIZABLE, Boolean.TRUE);

	        Point size = new Point(400, 300);
	        editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_SIZE, size);

	        int screenWidth = display.getBounds().width;
	        int screenHeight = display.getBounds().height;
	        Point location = new Point(
	                (screenWidth / (2 * display.getMonitors().length)) - (size.x / 2),
	                (screenHeight / 2) - (size.y / 2));
	        editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_LOCATION, location);

	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.EDIT_DIALOG_SETTINGS,
	                editDialogSettings,
	                DisplayMode.EDIT,
	                columnLabel);
	    }
	    
		private void registerRvaColumn(IConfigRegistry configRegistry, String columnLabel, String defaultValue) {
			// edit or not
			configRegistry.registerConfigAttribute(
					EditConfigAttributes.CELL_EDITABLE_RULE, 
					new IEditableRule() {
						@Override
						public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry) {
							return isEditable(cell.getColumnIndex(), cell.getRowIndex());
						}
						@Override
						public boolean isEditable(int columnIndex, int rowIndex) {
							return dataProvider.isRVA(columnIndex);
						}
					}, 
					DisplayMode.EDIT, 
					columnLabel);
			
			// Button displayed if editable
			ImagePainter imagePainter = new ImagePainter(IconLoader.loadIcon("table"));
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_PAINTER,
	                imagePainter,
	                DisplayMode.NORMAL,
	                "RVAeditor");

			// Custom dialog box
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new RvaCellEditor(Editor.this, defaultValue),
	                DisplayMode.EDIT,
	                columnLabel);
		}
	}
	
	class PopupEditorConfiguration extends AbstractRegistryConfiguration {
	    @Override
	    public void configureRegistry(IConfigRegistry configRegistry) {
	        // always/never open in a subdialog depending on popupEdit value
	    	configRegistry.unregisterConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG);
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.OPEN_IN_DIALOG,
	                popupEdit,
	                DisplayMode.EDIT);
	    }
	}
    
    public void refresh() {
    	table.refresh();
    }
    
	public Editor(Composite parent, DbConnection connection, String relvarName) {
		super(parent, connection, relvarName);
	}
	
	protected void init() {
	    
	    dataProvider = new DataProvider();
	    headingProvider = new HeadingProvider();
	    
        gridLayer = new DefaultGridLayer(
        		dataProvider,
                headingProvider
        );
        
        // CellLabelAccumulator determines how cells will be displayed
        class CellLabelAccumulator implements IConfigLabelAccumulator {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				configLabels.addLabel("column" + columnPosition);
				// error?
				if (dataProvider.getError(rowPosition) != null)
					configLabels.addLabel("error");
				// changed?
				else if (dataProvider.isChanged(columnPosition, rowPosition))
					configLabels.addLabel("changed");
				else if (dataProvider.isRVA(columnPosition))
					configLabels.addLabel("RVAeditor");
			}
        }
        
        DataLayer bodyDataLayer = (DataLayer)gridLayer.getBodyDataLayer();
        CellLabelAccumulator cellLabelAccumulator = new CellLabelAccumulator();
        bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);

        class HeadingLabelAccumulator implements IConfigLabelAccumulator {
        	@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
        		if (keys != null && keys.size() > 0) {
        			if (rowPosition == 0 && keys.get(0).contains(heading[columnPosition].getName()))
        				configLabels.addLabel("keycolumnintegrated");
        			else if (rowPosition >= 2 && keys.size() > 1 && keys.get(rowPosition - 1).contains(heading[columnPosition].getName()))
        				configLabels.addLabel("keycolumnalone");
        		}
			}
        }
        
        DataLayer headingDataLayer = (DataLayer)gridLayer.getColumnHeaderDataLayer();
        HeadingLabelAccumulator columnLabelAccumulator = new HeadingLabelAccumulator();
        headingDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
        
        table = new NatTable(parent, gridLayer, false);
        
        DefaultNatTableStyleConfiguration defaultStyle = new DefaultNatTableStyleConfiguration();
        table.addConfiguration(defaultStyle);
        table.addConfiguration(new EditorConfiguration()); 
        table.addConfiguration(new HeaderConfiguration());
        
        ContributionItem columnMenuItems = new ContributionItem() {
            @Override
        	public void fill(Menu menu, int index) {
            	MenuItem doesPopupEdit = new MenuItem(menu, SWT.CHECK);
            	doesPopupEdit.setText("Pop-up Edit Box");
            	doesPopupEdit.setSelection(popupEdit);
            	doesPopupEdit.addSelectionListener(new SelectionAdapter() {
            		public void widgetSelected(SelectionEvent evt) {
            			popupEdit = !popupEdit;
            			table.addConfiguration(new PopupEditorConfiguration());
            			table.configure();
            		}
            	});
            	MenuItem export = new MenuItem(menu, SWT.PUSH);
            	export.setText("Export");
            	export.addSelectionListener(new SelectionAdapter() {
            		public void widgetSelected(SelectionEvent evt) {
            			export();
            		}
            	});
            }
        };
		table.addConfiguration(new MenuConfiguration(
				GridRegion.COLUMN_HEADER, 
				new PopupMenuBuilder(table).withContributionItem(columnMenuItems)));

        ContributionItem rowMenuItems = new ContributionItem() {
            @Override
        	public void fill(Menu menu, int index) {
            	MenuItem doesDelete = new MenuItem(menu, SWT.PUSH);
            	doesDelete.setText("Delete");
            	doesDelete.addSelectionListener(new SelectionAdapter() {
            		public void widgetSelected(SelectionEvent evt) {
            			askDeleteSelected();
            		}
            	});
            	MenuItem export = new MenuItem(menu, SWT.PUSH);
            	export.setText("Export");
            	export.addSelectionListener(new SelectionAdapter() {
            		public void widgetSelected(SelectionEvent evt) {
            			export();
            		}
            	});
            }
        };
		table.addConfiguration(new MenuConfiguration(
				GridRegion.ROW_HEADER, 
				new PopupMenuBuilder(table).withContributionItem(rowMenuItems)));
				
		// Report row selection events, to help control updating
		table.addLayerListener(new ILayerListener() {
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof RowSelectionEvent) {
					rowBeenSelected(-1);
				} else if (event instanceof CellSelectionEvent) {
					CellSelectionEvent csEvent = (CellSelectionEvent) event;
					int row = LayerUtil.convertRowPosition(csEvent.getLayer(), csEvent.getRowPosition(), gridLayer.getBodyDataLayer());
					rowBeenSelected(row);								
				} else if (event instanceof CellVisualChangeEvent) {
					CellVisualChangeEvent cvEvent = (CellVisualChangeEvent)event;
					int row = LayerUtil.convertRowPosition(cvEvent.getLayer(), cvEvent.getRowPosition(), gridLayer.getBodyDataLayer());
					rowBeenSelected(row);								
				} else if (event instanceof CellEditorCreatedEvent) {
				} else {
					rowBeenSelected(-1);
				}
			}
		});
		
		// Tabbing wraps and moves up/down
		gridLayer.registerCommandHandler(
			    new MoveCellSelectionCommandHandler(gridLayer.getBodyLayer().getSelectionLayer(), 
			    		ITraversalStrategy.TABLE_CYCLE_TRAVERSAL_STRATEGY));
		
        table.configure();
        
        table.getDisplay().addFilter(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (table.isDisposed())
					return;
				if (!hasFocus(table))
					lostFocus();
			}
        });

        // Tooltip shows dataProvider update errors
		new DefaultToolTip(table, ToolTip.NO_RECREATE, false) {
			@Override
			protected Object getToolTipArea(Event event) {
				int x = table.getColumnPositionByX(event.x);
				int y = table.getRowPositionByY(event.y);
				return new Point(x, y);
			}

			@Override
			protected String getText(Event event) {
				int x = table.getColumnPositionByX(event.x);
				int y = table.getRowPositionByY(event.y);
				ILayerCell cell = table.getCellByPosition(x, y);
				if (cell == null)
					return null;
				int row = cell.getRowIndex();
				return dataProvider.getError(row);
			}
			
			@Override
			protected boolean shouldCreateToolTip(Event event) {
				if (getText(event) != null)
					return super.shouldCreateToolTip(event);
				return false;
			}
		};
	}
	
	public void export() {
		ExportCommand cmd = new ExportCommand(table.getConfigRegistry(), table.getShell());
		table.doCommand(cmd);
	}
	
	public void processDirtyRows() {
		dataProvider.processDirtyRows();		
		lastRowSelected = -1;
	}
	
	public int countDirtyRows() {
		return dataProvider.countDirtyRows();
	}
	
	private void editorBeenOpened(int row, int column) {
		lastRowSelected = row;
		processDirtyRows();
	}
	
	private void editorBeenClosed(int row, int column) {
		lastRowSelected = row;
		processDirtyRows();
	}
	
	private void rowBeenSelected(int row) {
		lastRowSelected = row;
		processDirtyRows();
	}
	
	private void lostFocus() {
		lastRowSelected = -1;
		processDirtyRows();
	}
	
	public Control getControl() {
		return table;
	}
	
	// Recursively determine if control or one of its children have the keyboard focus.
	public static boolean hasFocus(Control control) {
		if (control.isFocusControl())
			return true;
		if (control instanceof Composite)
			for (Control child: ((Composite)control).getChildren())
				if (hasFocus(child))
					return true;
		return false;
	}
	
	protected abstract String getAttributeSource();
	
	protected Tuples obtainTuples() {
		return connection.getTuples(getAttributeSource());
	}
	
	public void goToInsertRow() {
		ShowRowInViewportCommand cmd = new ShowRowInViewportCommand(gridLayer.getBodyLayer(), dataProvider.getRowCount() - 1);
		table.doCommand(cmd);
	}

	private void doDeleteSelected() {
		Set<Range> selections = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowPositions();
		dataProvider.deleteRows(selections);
	}
	
	public void askDeleteSelected() {
		if (countDirtyRows() > 0 && 
		    !MessageDialog.openConfirm(
		    		table.getShell(), 
		    		"Unsaved Changes", 
		    		"There are unsaved changes. If you proceed with deletion, they will be cancelled.\n\nPress OK to cancel unsaved changes and proceed with deletion."))
				return;
		if (askDeleteConfirm) {
			int selectedRowCount = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowCount();
			DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(table.getShell(), selectedRowCount, "tuple");
			if (deleteConfirmDialog.open() == DeleteConfirmDialog.OK) {
				askDeleteConfirm = deleteConfirmDialog.getAskDeleteConfirm();
				doDeleteSelected();
			}
		} else
			doDeleteSelected();
	}

}