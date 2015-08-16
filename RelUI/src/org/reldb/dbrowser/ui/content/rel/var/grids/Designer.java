package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;

public abstract class Designer extends Grid {

	protected NatTable table;
	
	protected DataProvider dataProvider;
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
	        	addColumn(column);
	    }

	    public void addColumn(int column) {
        	String columnLabel = "column" + column;
        	switch (column) {
        	case 0: registerAttributeNameColumn(registry, columnLabel); break;
        	case 1: registerTypeNameColumn(registry, columnLabel); break;
        	case 2: registerTypeDefinitionColumn(registry, columnLabel); break;
        	default: registerKeyColumn(registry, columnLabel);
        	}
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
		
		private void registerTypeDefinitionColumn(IConfigRegistry configRegistry, String columnLabel) {
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
							return dataProvider.isEditableNonscalarDefinition(rowIndex);
						}
					}, 
					DisplayMode.EDIT, 
					columnLabel);
			
			// Custom dialog box
	        configRegistry.registerConfigAttribute(
	                EditConfigAttributes.CELL_EDITOR,
	                new AttributeDesignerCellEditor(Designer.this),
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
			case 2: return "Definition";
			default: return (columnIndex == getColumnCount() - 1 && columnIndex > 3) ? "New Key" : "Key " + (columnIndex - 2);
			}
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return 3 + ((keys != null) ? keys.size() : 0);
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
			case 1: return (newTypeName != null) ? newTypeName : oldTypeName;
			default: return isEditableNonscalarDefinition() ? ((newDefinition != null) ? newDefinition : oldType) : "";
			}
		}
		
		void setColumnValue(int column, Object newValue) {
			if (newValue == null)
				return;
			switch (column) {
			case 0: newName = newValue.toString(); break; 
			case 1: 
				if (newTypeName != null && newTypeName.equals(newValue.toString()))
					return;
				newTypeName = newValue.toString();
				newDefinition = "";
				break;
			default: newDefinition = newValue.toString();
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
		
		boolean isEditableNonscalarDefinition() {
			Object typeName = getColumnValue(1);
			if (typeName == null)
				return false;
			return isFilled() && isNonScalar(typeName.toString());
		}

		boolean isFilled() {
			return (getColumnValue(0) != null && getColumnValue(0).toString().length() > 0 && 
					getColumnValue(1) != null && getColumnValue(1).toString().length() > 0);
		}
	}

    class DataProvider implements IDataProvider {
    	
    	private Vector<Attribute> cache = new Vector<Attribute>();
    	private int lastColumnIndex = 0;
    	
    	public DataProvider() {
    		reload();
    		lastColumnIndex = 3 + ((keys != null) ? keys.size() : 0) - 1;
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
			if (columnIndex < 3)
				return cache.get(rowIndex).getColumnValue(columnIndex);
			else
				return keys.get(columnIndex - 3).contains(cache.get(rowIndex).getName());
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			Attribute attribute = cache.get(rowIndex);
			if (columnIndex < 3) {
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
					keys.get(columnIndex - 3).remove(attribute.getName());
				else
					keys.get(columnIndex - 3).add(attribute.getName());
			}
			if (columnIndex >= lastColumnIndex && newValue != null && newValue.equals("true")) {
				keys.add(new HashSet<String>());
				lastColumnIndex++;
				editorConfiguration.addColumn(columnIndex);
				table.configure();
				table.refresh();
			} else if (columnIndex >= 3 && getKeyAttributeCount(columnIndex - 3) == 0) {
				keys.remove(columnIndex - 3);
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

		public boolean isEditableNonscalarDefinition(int rowIndex) {
			return cache.get(rowIndex).isEditableNonscalarDefinition();
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
					if (keys != null)
						for (HashSet<String> key: keys)
							key.remove(attribute.getName());
				else
					newCache.add(attribute);
				index++;
			}
			if (keys != null) {
				HashSet<HashSet<String>> newKeys = new HashSet<HashSet<String>>();
				for (HashSet<String> key: keys)
					if (key.size() > 0)
						newKeys.add(key);
				keys.clear();
				keys.addAll(newKeys);
				// Blank key definition allows user to add keys
				keys.add(new HashSet<String>());
			}
    		lastColumnIndex = 3 + ((keys != null) ? keys.size() : 0) - 1;
			cache = newCache;
			table.refresh();
		}
    };

	private boolean isNonScalar(String type) {
		return type.equals("RELATION") || type.equals("TUPLE") || type.equals("ARRAY");	
	}

	public abstract void refresh();
	
	// Relvar designer
	public Designer(Composite parent, DbConnection connection, String relvarName) {
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
	protected abstract Tuples obtainAttributes();

}
