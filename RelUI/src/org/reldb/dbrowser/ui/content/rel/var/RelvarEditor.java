package org.reldb.dbrowser.ui.content.rel.var;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
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
import org.eclipse.nebula.widgets.nattable.export.ExportConfigAttributes;
import org.eclipse.nebula.widgets.nattable.export.command.ExportCommand;
import org.eclipse.nebula.widgets.nattable.extension.poi.HSSFExcelExporter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
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

public class RelvarEditor {
	
	private Attribute[] heading;
	
	private Tuples tuples;
	
//	private static boolean askDeleteConfirm = true;

	private Vector<HashSet<String>> keys = new Vector<HashSet<String>>();
	
	private DbConnection connection;
	private String relvarName;
	
	private Composite content;
	private NatTable table;
	
	private DataProvider dataProvider;
	
	private boolean popupEdit = false;
	
	private Timer updateTimer = new Timer();
	
	class Row {
		private Vector<Object> originalData = new Vector<Object>();
		private HashMap<Integer, Object> newData = new HashMap<Integer, Object>();
		private String error = null;
		
		Row(Tuple tuple) {
			for (int column=0; column<tuple.getAttributeCount(); column++)
				originalData.add(tuple.get(column));
		}
		
		Row() {
			originalData = new Vector<Object>();
		}
		
		Object getColumnValue(int column) {
			Object v = newData.get(column);
			if (v != null)
				return v;
			if (column >= originalData.size())
				return null;
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
			error = null;
		}
		
		String getError() {
			return error;
		}
		
		void setError(String error) {
			this.error = error;
		}
	}
	
    class DataProvider implements IDataProvider {
    	
    	private HashSet<Integer> modifiedRows = new HashSet<Integer>();	    	
    	private Vector<Row> cache = new Vector<Row>();
    	private Row addRow = null;
    	
    	public DataProvider() {
    		Iterator<Tuple> iterator = tuples.iterator();
    		while (iterator.hasNext())
    			cache.add(new Row(iterator.next()));
    	}

		public boolean isChanged(int columnIndex, int rowIndex) {
			if (rowIndex >= cache.size())
				return addRow != null;
			return cache.get(rowIndex).isChanged(columnIndex);
		}
    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (rowIndex >= cache.size()) {
				if (addRow == null)
					return "";
				return addRow.getColumnValue(columnIndex);
			}
			return cache.get(rowIndex).getColumnValue(columnIndex);
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			if (heading[columnIndex].getType().toString().equals("CHARACTER")) {
				if (newValue == null)
					newValue = "";
			} else if (newValue == null || newValue.toString().length() == 0)
				return;
			if (rowIndex >= cache.size()) {
				if (addRow == null)
					addRow = new Row();
				addRow.setColumnValue(columnIndex, newValue.toString());
				return;
			}
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
			return cache.size() + 1;
		}

		public void processDirtyRows() {
			if (modifiedRows.size() > 0)
				System.out.println("NatTable: update changes - invoke committed() on each updated row and purge modifiedRows when done");
			if (addRow != null)
				System.out.println("NatTable: insert new data - move addRow to cache and null addRow when done");
			// if update fails, set cache.get(row).getError to non-null
		}

		public String getError(int row) {
			if (row >= cache.size())
				if (addRow != null)
					return addRow.getError();
				else
					return null;
			return cache.get(row).getError();
		}
    };
	
	public RelvarEditor(Composite parent, DbConnection connection, String relvarName) {
		this.connection = connection;
		this.relvarName = relvarName;
		
		content = new Composite(parent, SWT.None);
		content.setLayout(new FillLayout());
		
		refresh();
	}
	
	public void export() {
		ExportCommand cmd = new ExportCommand(table.getConfigRegistry(), table.getShell());
		table.doCommand(cmd);
	}
	
	private void processDirtyRows() {
		dataProvider.processDirtyRows();		
	}
	
	private void stopUpdateTimer() {
		updateTimer.cancel();		
	}
	
	private void startUpdateTimer() {
		updateTimer = new Timer();
		updateTimer.schedule(
			new TimerTask() {
				@Override
				public void run() {
					processDirtyRows();
				}
		    }, 
			1000);
	}
	
	private void editorOpen(int row, int column) {
		stopUpdateTimer();
	}
	
	private void editorClose(int row, int column) {
		stopUpdateTimer();
		startUpdateTimer();
	}
	
	private void lostFocus() {
		updateTimer.cancel();
		processDirtyRows();
	}
	
	public Control getControl() {
		return content;
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
	
	public void refresh() {		
		obtainKeyDefinitions();
		
		tuples = obtainTuples();

    	heading = tuples.getHeading().toArray();

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
    	                		editorOpen(getRowIndex(), getColumnIndex());
    	                		return super.activateCell(parent, originalCanonicalValue);
    	                	}
    	                	public void close() {
    	                		editorClose(getRowIndex(), getColumnIndex());
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
    	                new CheckBoxCellEditor() {
    	                	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
    	                		editorOpen(getRowIndex(), getColumnIndex());
    	                		return super.activateCell(parent, originalCanonicalValue);
    	                	}
    	                	public void close() {
    	                		editorClose(getRowIndex(), getColumnIndex());
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
    	                		editorOpen(getRowIndex(), getColumnIndex());
    	                		return super.activateCell(parent, originalCanonicalValue);
    	                	}
    	                	public void close() {
    	                		editorClose(getRowIndex(), getColumnIndex());
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
    	}
		
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
	    
	    dataProvider = new DataProvider();
	    HeadingProvider headingProvider = new HeadingProvider();
	    
        DefaultGridLayer gridLayer = new DefaultGridLayer(
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
				new PopupMenuBuilder(table).withContributionItem(contributionItem)));
	
        table.configure();
        
        table.getDisplay().addFilter(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event event) {
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