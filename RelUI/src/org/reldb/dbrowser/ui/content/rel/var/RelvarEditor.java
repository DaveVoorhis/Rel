package org.reldb.dbrowser.ui.content.rel.var;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.MultiLineTextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.gui.ICellEditDialog;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyleEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tickupdate.TickUpdateConfigAttributes;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelvarEditor {
	
	private static boolean askDeleteConfirm = true;

	private Vector<HashSet<String>> keys = new Vector<HashSet<String>>();
	
	private DbConnection connection;
	private String relvarName;
	
	private Composite content;
	private NatTable table;
	
	private boolean popupEdit = false;
	
	public RelvarEditor(Composite parent, DbConnection connection, String relvarName) {
		this.connection = connection;
		this.relvarName = relvarName;
		
		content = new Composite(parent, SWT.None);
		content.setLayout(new FillLayout());
		
		refresh();
	}
	
	public Control getControl() {
		return content;
	}
	
	public void refresh() {		
		obtainKeyDefinitions();
		
		Tuples tuples = obtainTuples();

    	Attribute[] heading = tuples.getHeading().toArray();

    	// IConfiguration for registering a UI binding to open a menu
    	class MenuConfiguration extends AbstractUiBindingConfiguration {
    	    private final Menu menu;
    	    private final String gridRegion;
    	 
    	    // gridRegion can be, for example, GridRegion.COLUMN_HEADER
    	    public MenuConfiguration(String gridRegion, PopupMenuBuilder menuBuilder) {
    	        this.gridRegion = gridRegion;
    	        // create the menu using the PopupMenuBuilder
    	        menu = menuBuilder.build();
    	    }
    	    
    	    @Override
    	    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
    	        uiBindingRegistry.registerMouseDownBinding(
    	                new MouseEventMatcher(SWT.NONE, gridRegion, MouseEventMatcher.RIGHT_BUTTON),
    	                new PopupMenuAction(menu));
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
    	        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
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
    	        Style selectStyle = new Style();
    			configRegistry.registerConfigAttribute(
    					CellConfigAttributes.CELL_STYLE, 
    					selectStyle, 
    					DisplayMode.SELECT);
    	        configRegistry.registerConfigAttribute(
    	                EditConfigAttributes.CELL_EDITOR,
    	                new TextCellEditor(true, true), DisplayMode.NORMAL);
    	        configRegistry.registerConfigAttribute(
    	                EditConfigAttributes.OPEN_ADJACENT_EDITOR,
    	                Boolean.TRUE,
    	                DisplayMode.EDIT);
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
					else
						registerDefaultColumn(configRegistry, columnLabel);
    	        }
    	    }

			private void registerDefaultColumn(IConfigRegistry configRegistry, String columnLabel) {
    	    }
    	    
    	    private void registerBooleanColumn(IConfigRegistry configRegistry, String columnLabel) {
    	        // register a CheckBoxCellEditor
    	        configRegistry.registerConfigAttribute(
    	                EditConfigAttributes.CELL_EDITOR,
    	                new CheckBoxCellEditor(),
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

    	        // don't forget to register the Double converter!
    	        configRegistry.registerConfigAttribute(
    	                CellConfigAttributes.DISPLAY_CONVERTER,
    	                new DefaultDoubleDisplayConverter(),
    	                DisplayMode.NORMAL,
    	                columnLabel);
    	    }

    	    private void registerIntegerColumn(IConfigRegistry configRegistry, String columnLabel) {
    	        // don't forget to register the Integer converter!
    	        configRegistry.registerConfigAttribute(
    	                CellConfigAttributes.DISPLAY_CONVERTER,
    	                new DefaultIntegerDisplayConverter(),
    	                DisplayMode.NORMAL,
    	                columnLabel);
    	    }

    	    private void registerMultiLineEditorColumn(IConfigRegistry configRegistry, String columnLabel) {
    	        // configure the multi line text editor for column four
    	        configRegistry.registerConfigAttribute(
    	                EditConfigAttributes.CELL_EDITOR,
    	                new MultiLineTextCellEditor(false),
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

    	}

    	class Row {
    		private Vector<Object> originalData = new Vector<Object>();
    		private HashMap<Integer, Object> newData = new HashMap<Integer, Object>();
    		
    		Row(Tuple tuple) {
    			for (int column=0; column<tuple.getAttributeCount(); column++)
    				originalData.add(tuple.get(column));
    		}
    		
    		Object getColumnValue(int column) {
    			Object v = newData.get(column);
    			if (v != null)
    				return v;
    			return originalData.get(column);
    		}
    		
    		void setColumnValue(int column, Object newValue) {
    			newData.put(Integer.valueOf(column), newValue);
    		}

			boolean isChanged(int columnIndex) {
				return newData.containsKey(columnIndex);
			}
			
			// Copy new data to original data, and clear new data
			void committed() {
				for (Entry<Integer, Object> entry: newData.entrySet())
					originalData.set(entry.getKey(), entry.getValue());
				newData.clear();
			}
			
			// Flush new data
			public void cancelled() {
				newData.clear();
			}
    	}
    	
	    class DataProvider implements IDataProvider {
	    	
	    	private HashSet<Integer> modifiedRows = new HashSet<Integer>();	    	
	    	private Vector<Row> cache = new Vector<Row>();
	    	
	    	public DataProvider() {
	    		Iterator<Tuple> iterator = tuples.iterator();
	    		while (iterator.hasNext())
	    			cache.add(new Row(iterator.next()));
	    	}

			public boolean isChanged(int columnIndex, int rowIndex) {
				return cache.get(rowIndex).isChanged(columnIndex);
			}
	    	
			@Override
			public Object getDataValue(int columnIndex, int rowIndex) {
				return cache.get(rowIndex).getColumnValue(columnIndex);
			}

			@Override
			public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
				if (newValue.toString().equals(getDataValue(columnIndex, rowIndex).toString()))
					return;
				cache.get(rowIndex).setColumnValue(columnIndex, newValue.toString());
				modifiedRows.add(rowIndex);
			}

			@Override
			public int getColumnCount() {
				return heading.length;
			}

			@Override
			public int getRowCount() {
				return cache.size();
			}

			public void processDirtyRows() {
				if (modifiedRows.size() > 0)
					System.out.println("NatTable: process unsaved data");
			}
	    };
		
	    class HeadingProvider implements IDataProvider {	    	
			@Override
			public Object getDataValue(int columnIndex, int rowIndex) {
				Attribute attribute = heading[columnIndex];
				switch (rowIndex) {
					case 0:
						return attribute.getName();
					case 1:
						return attribute.getType().toString();
					default:
						return "";
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
				return 2 + ((keys.size() > 1) ? keys.size() - 1 : 0);
			}
	    };
	    
	    DataProvider dataProvider = new DataProvider();
	    
        DefaultGridLayer gridLayer = new DefaultGridLayer(
        		dataProvider,
                new HeadingProvider()
        );
        
        class CellLabelAccumulator implements IConfigLabelAccumulator {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				configLabels.addLabel("column" + columnPosition);
				if (dataProvider.isChanged(columnPosition, rowPosition))
					configLabels.addLabel("changed");
			}
        }
        
        DataLayer bodyDataLayer = (DataLayer)gridLayer.getBodyDataLayer();
        CellLabelAccumulator cellLabelAccumulator = new CellLabelAccumulator();
        bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);

        class HeadingLabelAccumulator implements IConfigLabelAccumulator {
        	@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
        		if (keys.size() > 0) {
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
        
        for (Control control: content.getChildren())
        	control.dispose();
        
        table = new NatTable(content, gridLayer, false);
        
		table.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				dataProvider.processDirtyRows();
			}
			public void focusGained(FocusEvent e) {
				dataProvider.processDirtyRows();
			}
		});
		
        table.addLayerListener(new ILayerListener() {
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof CellSelectionEvent) {
					CellSelectionEvent csEvent = (CellSelectionEvent)event;
					csEvent.convertToLocal(csEvent.getSelectionLayer());
					System.out.println("RelvarEditor: row selected is " + csEvent.getRowPosition());
					dataProvider.processDirtyRows();
				}
			}
        });
        
        DefaultNatTableStyleConfiguration defaultStyle = new DefaultNatTableStyleConfiguration();
        table.addConfiguration(defaultStyle);
        table.addConfiguration(new EditorConfiguration()); 
        table.addConfiguration(new HeaderConfiguration());
        
        ContributionItem contributionItem = new ContributionItem() {
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
            }
        };
		table.addConfiguration(new MenuConfiguration(
				GridRegion.COLUMN_HEADER, 
				new PopupMenuBuilder(table).withContributionItem(contributionItem)));
	
        table.configure();	
	}

	private void obtainKeyDefinitions() {
		keys.clear();
		Tuples keyDefinitions = (Tuples)connection.evaluate("((sys.Catalog WHERE Name = '" + relvarName + "') {Keys}) UNGROUP Keys");
		for (Tuple keyDefinition: keyDefinitions) {
			Tuples keyAttributes = (Tuples)(keyDefinition.get("Attributes"));
			HashSet<String> keyAttributeNames = new HashSet<String>();
			for (Tuple keyAttribute: keyAttributes) {
				String name = keyAttribute.get("Name").toString();
				keyAttributeNames.add(name);
			}
			keys.add(keyAttributeNames);
		}
	}
	
	private Tuples obtainTuples() {
		return connection.getTuples(relvarName);
	}
	
/*	
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
*/

	public void goToInsertRow() {
		// TODO Auto-generated method stub
		
	}

	public void askDeleteSelected() {
		// TODO Auto-generated method stub
		
	}

}