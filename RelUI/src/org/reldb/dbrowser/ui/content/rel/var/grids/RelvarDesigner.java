package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.MultiLineTextCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.gui.ICellEditDialog;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.selection.ITraversalStrategy;
import org.eclipse.nebula.widgets.nattable.selection.MoveCellSelectionCommandHandler;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;

public class RelvarDesigner extends RelvarUI {
	
	private NatTable table;
	
	private DataProvider dataProvider;
	private HeadingProvider headingProvider;
	private DefaultGridLayer gridLayer;
	private EditorConfiguration editorConfiguration;

	class EditorConfiguration extends AbstractRegistryConfiguration {
		private IConfigRegistry registry;
		
	    @Override
	    public void configureRegistry(IConfigRegistry configRegistry) {
	    	registry = configRegistry;
	    	// editable
	        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
	        // style for selected cells
	        Style selectStyle = new Style();
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_STYLE, 
					selectStyle, 
					DisplayMode.SELECT);
	        // open adjacent editor when we leave the current one during editing
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.OPEN_ADJACENT_EDITOR,
	                Boolean.TRUE,
	                DisplayMode.EDIT);
	        // style for upper left corner
	        BorderStyle borderStyle = new BorderStyle();
	        borderStyle.setColor(GUIHelper.COLOR_GRAY);
			configRegistry.registerConfigAttribute(
					CellConfigAttributes.CELL_PAINTER, 
					new LineBorderDecorator(new TextPainter(), borderStyle), 
					DisplayMode.NORMAL, 
					GridRegion.CORNER);
	        // for each column...
	        for (int column = 0; column < headingProvider.getColumnCount(); column++)
	        	addColumn(column, headingProvider.getDataValue(1, 0).toString());
	    }

	    public void addColumn(int column, String type) {
        	String columnLabel = "column" + column;
        	if (column == 0)
        		registerAttributeNameColumn(registry, columnLabel);
        	else if (column == 1)
        		if (type.startsWith("RELATION ") || type.startsWith("TUPLE ") || type.startsWith("ARRAY "))
        			registerTypeNonscalarColumn(registry, columnLabel, type.substring(0, type.indexOf(' ')));
        		else
        			registerTypeNameColumn(registry, columnLabel);
        	else
        		registerKeyColumn(registry, columnLabel);    	    	
	    }
	    
		private void registerAttributeNameColumn(IConfigRegistry configRegistry, String columnLabel) {
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
		
		private void registerTypeNameColumn(IConfigRegistry configRegistry, String columnLabel) {
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
	        // use a combobox
	        configRegistry.registerConfigAttribute(
    	        	EditConfigAttributes.CELL_EDITOR, 
    	        	new ComboBoxCellEditor(getTypes()), 
    	        	DisplayMode.EDIT, 
    	        	columnLabel);    	        			
	        configRegistry.registerConfigAttribute(
    	        	CellConfigAttributes.CELL_PAINTER, 
    	        	new ComboBoxPainter(),
    	        	DisplayMode.EDIT, 
    	        	columnLabel);    	    
        }
	    
		private void registerTypeNonscalarColumn(IConfigRegistry configRegistry, String columnLabel, String typeKind) {
	        // configure the multi line text editor
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new MultiLineTextCellEditor(false),
	                DisplayMode.NORMAL,
	                columnLabel);

	        // popup
	    	configRegistry.unregisterConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG);
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.OPEN_IN_DIALOG,
	                true,
	                DisplayMode.EDIT,
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
		
	    private void registerKeyColumn(IConfigRegistry configRegistry, String columnLabel) {
	        configRegistry.registerConfigAttribute(
	        		EditConfigAttributes.CELL_EDITOR, 
	        		new CheckBoxCellEditor(), 
	        		DisplayMode.EDIT, 
	        		columnLabel);
        	configRegistry.registerConfigAttribute(
	        		CellConfigAttributes.CELL_PAINTER, 
	        		new CheckBoxPainter(), 
	        		DisplayMode.NORMAL, 
	        		columnLabel);    	    
        }
	}

    class HeadingProvider implements IDataProvider {	    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			switch (columnIndex) {
			case 0: return "Name";
			case 1: return "Type";
			default: return (columnIndex == getColumnCount() - 1 && columnIndex > 2) ? "New Key" : "Key " + (columnIndex - 1);
			}
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return 2 + keys.size();
		}

		@Override
		public int getRowCount() {
			return 1;
		}
    };
	
	class Attribute {
		private String oldName;
		private String oldTypeName;
		private Value oldType;

		private String newName;
		private String newTypeName;
		private String newDefinition;
		
		Attribute(Tuple tuple) {
			oldName = tuple.get(0).toString();
			oldTypeName = tuple.get(1).toString();
			oldType = tuple.get(2);
			reset();
		}
		
		Attribute() {
			oldName = null;
			oldTypeName = null;
			oldType = null;
			reset();
		}
		
		Object getColumnValue(int column) {
			switch (column) {
			case 0: return (newName != null) ? newName : oldName;
			default: return (newTypeName != null) ? newTypeName : oldTypeName;
			}
		}
		
		void setColumnValue(int column, Object newValue) {
			switch (column) {
			case 0: newName = (newValue == null) ? null : newValue.toString(); break; 
			default: newTypeName = (newValue == null) ? null : newValue.toString(); break;
			}
		}
		
		String getName() {
			if (newName == null) {
				if (oldName == null)
					return "";
				return oldName;
			}
			return newName;
		}
		
		private void reset() {
			newName = null;
			newTypeName = null;
			newDefinition = null;
		}

		public boolean isFilled() {
			return (getColumnValue(0) != null && getColumnValue(0).toString().length() > 0 && 
					getColumnValue(1) != null && getColumnValue(1).toString().length() > 0);
		}
	}

    class DataProvider implements IDataProvider {
    	
    	private Vector<Attribute> cache = new Vector<Attribute>();
    	private int lastColumnIndex = 0;
    	
    	public DataProvider() {
    		reload();
    		lastColumnIndex = 2 + keys.size() - 1;
    	}

		public void reload() {
			cache.clear();
			Tuples tuples = obtainAttributes();
    		Iterator<Tuple> iterator = tuples.iterator();
    		while (iterator.hasNext())
    			cache.add(new Attribute(iterator.next()));
    		cache.add(new Attribute());			
		}
    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (columnIndex < 2)
				return cache.get(rowIndex).getColumnValue(columnIndex);
			else
				return keys.get(columnIndex - 2).contains(cache.get(rowIndex).getName());
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			Attribute attribute = cache.get(rowIndex);
			if (columnIndex < 2) {
				if (newValue == null || newValue.toString().length() == 0)
					return;
				if (columnIndex == 0) {
					for (HashSet<String> key: keys) {
						if (key.contains(attribute.getName())) {
							key.remove(attribute.getName());
							key.add(newValue.toString());
						}
					}
				}
				attribute.setColumnValue(columnIndex, newValue);
			} else {
				if (rowIndex == getRowCount() - 1 && !attribute.isFilled())
					return;
				if (newValue == null)
					newValue = "false";
				if (newValue.equals("false"))
					keys.get(columnIndex - 2).remove(attribute.getName());
				else
					keys.get(columnIndex - 2).add(attribute.getName());
			}
			if (columnIndex >= lastColumnIndex && newValue != null && newValue.equals("true")) {
				keys.add(new HashSet<String>());
				lastColumnIndex++;
				editorConfiguration.addColumn(columnIndex, "");
				table.configure();
				table.refresh();
			} else if (columnIndex >= 2 && getKeyAttributeCount(columnIndex - 2) == 0) {
				keys.remove(columnIndex - 2);
				lastColumnIndex--;
				table.configure();
				table.refresh();
			}
			int lastRowIndex = cache.size() - 1;
			if (rowIndex == lastRowIndex && cache.get(lastRowIndex).isFilled()) {
				cache.add(new Attribute());
				table.redraw();
			}
		}

		@Override
		public int getColumnCount() {
			return headingProvider.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return cache.size();
		}
		
		public int getKeyAttributeCount(int keyColumn) {
			return keys.get(keyColumn).size();
		}

		public void deleteRows(Set<Range> selections) {
			HashSet<Integer> deleteMe = new HashSet<Integer>();
			for (Range range: selections)
				for (int n = range.start; n < range.end; n++)
					deleteMe.add(n);
			Vector<Attribute> newCache = new Vector<Attribute>();
			int index = 0;
			for (Attribute attribute: cache) {
				if (deleteMe.contains(index))
					for (HashSet<String> key: keys)
						key.remove(attribute.getName());
				else
					newCache.add(attribute);
				index++;
			}
			HashSet<HashSet<String>> newKeys = new HashSet<HashSet<String>>();
			for (HashSet<String> key: keys)
				if (key.size() > 0)
					newKeys.add(key);
			keys.clear();
			keys.addAll(newKeys);
			// Blank key definition allows user to add keys
			keys.add(new HashSet<String>());
    		lastColumnIndex = 2 + keys.size() - 1;
			cache = newCache;
			table.refresh();
		}
    };
	
	private void syncFromDatabase() {
		obtainKeyDefinitions();		
		// Blank key definition allows user to add keys
		keys.add(new HashSet<String>());
	}

	public void refresh() {
		syncFromDatabase();
    	dataProvider.reload();
    	table.refresh();		
	}

	public RelvarDesigner(Composite parent, DbConnection connection, String relvarName) {
		super(parent, connection, relvarName);

	    syncFromDatabase();
		
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
			}
        }
        
        DataLayer bodyDataLayer = (DataLayer)gridLayer.getBodyDataLayer();
        CellLabelAccumulator cellLabelAccumulator = new CellLabelAccumulator();
        bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
		
        table = new NatTable(parent, gridLayer, false);
        
        editorConfiguration = new EditorConfiguration();
        
        DefaultNatTableStyleConfiguration defaultStyle = new DefaultNatTableStyleConfiguration();
        table.addConfiguration(defaultStyle);
        table.addConfiguration(editorConfiguration); 
        
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
            }
        };
		table.addConfiguration(new MenuConfiguration(
				GridRegion.ROW_HEADER, 
				new PopupMenuBuilder(table).withContributionItem(rowMenuItems)));
		
		// Tabbing wraps and moves up/down
		gridLayer.registerCommandHandler(
			    new MoveCellSelectionCommandHandler(gridLayer.getBodyLayer().getSelectionLayer(), 
			    		ITraversalStrategy.TABLE_CYCLE_TRAVERSAL_STRATEGY));
                
        table.configure();
	}

	private void doDeleteSelected() {
		Set<Range> selections = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowPositions();
		dataProvider.deleteRows(selections);
	}
	
	public void askDeleteSelected() {
		if (askDeleteConfirm) {
			int selectedRowCount = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowCount();
			DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(table.getShell(), selectedRowCount, "attribute");
			if (deleteConfirmDialog.open() == DeleteConfirmDialog.OK) {
				askDeleteConfirm = deleteConfirmDialog.getAskDeleteConfirm();
				doDeleteSelected();
			}
		}
	}
	
	public Control getControl() {
		return table;
	}
	
	protected List<String> getTypes() {
		Vector<String> types = new Vector<String>();
		Tuples typeNames = connection.getTuples("sys.Types {Name}");
		for (Tuple typeName: typeNames) 
			types.add(typeName.get("Name").toString());
		types.add("RELATION");
		types.add("TUPLE");
		types.sort(null);
		return types;
	}
	
	// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
	private Tuples obtainAttributes() {
		return connection.getTuples(
				"EXTEND THE_Attributes(Attributes FROM TUPLE FROM (sys.Catalog WHERE Name='" + relvarName + "')): " +
				"{AttrTypeName := " +
				"	IF IS_Scalar(AttrType) THEN " +
				"		THE_TypeName(TREAT_AS_Scalar(AttrType)) " + 
				"	ELSE " +
				"		THE_Kind(TREAT_AS_NonScalar(AttrType)) " + 
				"	END IF} " +
				"{AttrName, AttrTypeName, AttrType}");
	}
	
}