package org.reldb.dbrowser.ui.content.rel.var.grids;

import java.util.HashMap;
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
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
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
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

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
        	case Attribute.NAME_COLUMN: registerAttributeNameColumn(registry, columnLabel); break;
        	case Attribute.TYPE_COLUMN: registerTypeNameColumn(registry, columnLabel); break;
        	case Attribute.HEADING_COLUMN: registerTypeDefinitionColumn(registry, columnLabel); break;
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
			
			// Button displayed if editable
			ImagePainter imagePainter = new ImagePainter(IconLoader.loadIcon("item_design"));
	        configRegistry.registerConfigAttribute(
	                CellConfigAttributes.CELL_PAINTER,
	                imagePainter,
	                DisplayMode.NORMAL,
	                "nonscalareditor");

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
			case Attribute.NAME_COLUMN: return "Name";
			case Attribute.TYPE_COLUMN: return "Type";
			case Attribute.HEADING_COLUMN: return "Heading";
			default: return (columnIndex == getColumnCount() - 1 && columnIndex > Attribute.COLUMN_COUNT) 
						? "New Key" 
						: "Key " + (columnIndex - (Attribute.COLUMN_COUNT - 1));
			}
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return dataProvider.getLastColumnIndex() + 1;
		}

		@Override
		public int getRowCount() {
			return 1;
		}
    };
    
    /** Override to be notified when the relvar definition has (probably) changed. */
    protected void changedDefinition() {}
    
	class DataProvider implements IDataProvider {
    	
    	private Vector<Attribute> data;
    	private int lastColumnIndex = 0;
    	private String kind;
    	
    	private int getLastColumnIndex() {
    		return Attribute.COLUMN_COUNT + ((keys != null) ? keys.size() : 0) - 1;
    	}
    	
    	public DataProvider() {
    		reload();
    		lastColumnIndex = getLastColumnIndex();
    	}
    	
    	private String getKindFor(String typeInfo) {
    		return connection.evaluate("THE_Kind(" + typeInfo + ")").toString();	
    	}
    	
    	// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
    	private Tuples getAttributesFor(String typeInfo) {
    		return connection.getTuples(
    				"EXTEND THE_Attributes(" + typeInfo + "): " +
    				"{AttrTypeName := " +
    				"	IF IS_Scalar(AttrType) THEN " +
    				"		THE_TypeName(TREAT_AS_Scalar(AttrType)) " + 
    				"	ELSE " +
    				"		THE_Kind(TREAT_AS_NonScalar(AttrType)) " + 
    				"	END IF} " +
    				"{AttrName, AttrTypeName, AttrType}");
    	}
    	
    	private String getKind() {
    		if (getAttributeSource().length() == 0)
    			return null;
    		return getKindFor(getAttributeSource());
    	}
    	
    	// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
    	private Tuples getAttributes() {
    		if (getAttributeSource().length() == 0)
    			return null;
    		return getAttributesFor(getAttributeSource());
    	}

		public void reload() {
			kind = getKind();
			data = new Vector<Attribute>();
			Tuples tuples = getAttributes();
			if (tuples != null) {
	    		Iterator<Tuple> iterator = tuples.iterator();
	    		while (iterator.hasNext())
	    			data.add(new Attribute(iterator.next()));
			}
			data.add(new Attribute());
		}
    	
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (columnIndex < Attribute.COLUMN_COUNT)
				return data.get(rowIndex).getColumnValue(columnIndex);
			else
				return keys.get(columnIndex - Attribute.COLUMN_COUNT).contains(data.get(rowIndex).getName());
		}
		
		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			Attribute attribute = data.get(rowIndex);
			if (columnIndex < Attribute.COLUMN_COUNT) {
				if (newValue == null || newValue.toString().length() == 0)
					return;
				if (columnIndex == 0) {
					if (keys != null)
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
					keys.get(columnIndex - Attribute.COLUMN_COUNT).remove(attribute.getName());
				else
					keys.get(columnIndex - Attribute.COLUMN_COUNT).add(attribute.getName());
			}
			if (columnIndex >= lastColumnIndex && newValue != null && newValue.equals("true")) {
				keys.add(new HashSet<String>());
				lastColumnIndex++;
				editorConfiguration.addColumn(columnIndex);
				table.configure();
				table.refresh();
			} else if (columnIndex >= Attribute.COLUMN_COUNT && getKeyAttributeCount(columnIndex - Attribute.COLUMN_COUNT) == 0) {
				keys.remove(columnIndex - Attribute.COLUMN_COUNT);
				lastColumnIndex--;
				table.configure();
				table.refresh();
			}
			int lastRowIndex = data.size() - 1;
			if (rowIndex == lastRowIndex && data.get(lastRowIndex).isFilled()) {
				data.add(new Attribute());
				table.redraw();
			}
			changedDefinition();
		}
		
		@Override
		public int getColumnCount() {
			return headingProvider.getColumnCount();
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		public boolean isEditableNonscalarDefinition(int rowIndex) {
			return data.get(rowIndex).isEditableNonscalarDefinition();
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
			for (Attribute attribute: data) {
				if (deleteMe.contains(index)) {
					if (keys != null)
						for (HashSet<String> key: keys)
							key.remove(attribute.getName());
				} else
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
	    		lastColumnIndex = getLastColumnIndex();
			}
			data = newCache;
			table.refresh();
			changedDefinition();
		}

		// Convert this into a TypeInfo literal 
		public String getTypeInfoLiteral() {
			String body = "";
			for (Attribute attribute: data)
				if (attribute.isFilled())
					body += ((body.length() > 0) ? "," : "") + "\n\t" + attribute.getTypeInfoLiteral();
			return "NonScalar('" + kind + "', RELATION {AttrName CHAR, AttrType TypeInfo} {" + body + "})";
		}

		private String getRelKeysDefinition(Vector<HashSet<String>> keys) {
			String keysDef = "";
			if (keys.size() == 1)
				keysDef = "KEY {}";
			else
				for (int keyIndex = 0; keyIndex < keys.size() - 1; keyIndex++) {
					HashSet<String> key = keys.get(keyIndex);
					String keyDef = "";
					for (String keyAttribute: key) {
						keyDef += ((keyDef.length() > 0) ? ", " : "") + keyAttribute;
					}
					keysDef += ((keysDef.length() > 0) ? " " : "") + "KEY {" + keyDef + "}";
				}			
			return keysDef;
		}
		
		private String getRelKeysDefinition() {
			return getRelKeysDefinition(keys);
		}
		
		private String getHeadingDefinition(String typeInfo) {
			String kind = getKindFor(typeInfo);
			Tuples tuples = getAttributesFor(typeInfo);
			String body = "";
			if (tuples != null)				
				for (Tuple tuple: tuples) {
					String attrName = tuple.get(Attribute.NAME_COLUMN).toString();
					String type = tuple.get(Attribute.TYPE_COLUMN).toString();
					if (Attribute.isNonScalar(type))
						type = getHeadingDefinition(tuple.get(Attribute.HEADING_COLUMN).toString());
					body += ((body.length() > 0) ? ", " : "") + attrName + " " + type; 
				}
			return kind + " {" + body + "}";
		}
		
		private String getRelHeadingDefinition(Attribute a) {
			String typeInfo = a.getNewColumnValue(Attribute.HEADING_COLUMN);
			return getHeadingDefinition(typeInfo);
		}
		
		private String getRelAlterClause(Attribute a) {
			if (a.isNameChange() && a.isHeadingChange()) {
				return "DROP " + a.getOriginalColumnValue(Attribute.NAME_COLUMN) + "\n\t" + 
						"INSERT " + a.getName() + " " + getRelHeadingDefinition(a);			
			} else if (a.isNameChange() && a.isTypeNameChange() && !a.isHeadingChange()) {
				return "DROP " + a.getOriginalColumnValue(Attribute.NAME_COLUMN) + "\n\t" + 
						"INSERT " + a.getName() + " " + a.getNewColumnValue(Attribute.TYPE_COLUMN);			
			} else if (a.isNameChange() && !a.isTypeNameChange() && !a.isHeadingChange()) {
				return "RENAME " + a.getOriginalColumnValue(Attribute.NAME_COLUMN) + " TO " + a.getName();
			} else if (!a.isNameChange() && a.isHeadingChange()) {
				return "TYPE_OF " + a.getOriginalColumnValue(Attribute.NAME_COLUMN) + " TO " + getRelHeadingDefinition(a);
			} else if (!a.isNameChange() && a.isTypeNameChange() && !a.isHeadingChange()) {
				return "TYPE_OF " + a.getOriginalColumnValue(Attribute.NAME_COLUMN) + " TO " + a.getNewColumnValue(Attribute.TYPE_COLUMN);
			} else 
				return null;
		}

		private String getRelAddClause(Attribute a) {
			return "INSERT " + a.getName() + " " + 
				(a.isEditableNonscalarDefinition() 
					? getRelHeadingDefinition(a) 
					: a.getColumnValue(Attribute.TYPE_COLUMN));	
		}

		public String getRelDefinition() {
			// attributes
			Tuples existingDefinitionTuples = getAttributes();
			HashMap<String, Attribute> existingDefinition = new HashMap<String, Attribute>();
			if (existingDefinitionTuples != null) {
	    		Iterator<Tuple> iterator = existingDefinitionTuples.iterator();
	    		while (iterator.hasNext()) {
	    			Attribute attribute = new Attribute(iterator.next());
	    			existingDefinition.put(attribute.getName(), attribute);
	    		}
			}
			String body = "";
			for (Attribute attribute: data) {
				if (!attribute.isFilled())
					continue;
				String originalAttributeName = attribute.getOriginalColumnValue(Attribute.NAME_COLUMN);
				if (originalAttributeName != null && existingDefinition.containsKey(originalAttributeName)) {
					String alterClause = getRelAlterClause(attribute);
					if (alterClause != null)
						body += ((body.length() > 0) ? "\n" : "") + "\t" + alterClause;
					existingDefinition.remove(originalAttributeName);
				} else
					body += ((body.length() > 0) ? "\n" : "") + "\t" + getRelAddClause(attribute);
			}
			for (String attributeName: existingDefinition.keySet())
				body += ((body.length() > 0) ? "\n" : "") + "\t" + "DROP " + attributeName;
			// keys
			Vector<HashSet<String>> existingKeyDefinitions = getKeyDefinitions();
			HashSet<HashSet<String>> existingKeys = new HashSet<HashSet<String>>();
			if (existingKeyDefinitions != null) {
				existingKeys.addAll(existingKeyDefinitions);
				existingKeys.add(new HashSet<String>());
			}
			HashSet<HashSet<String>> newKeys = new HashSet<HashSet<String>>();
			newKeys.addAll(keys);
			if (!existingKeys.equals(newKeys))
				body += ((body.length() > 0) ? "\n" : "") + "\t" + getRelKeysDefinition();
			// produce output
			if (body.length() == 0)
				return "";
			return "ALTER " + relvarName + "\n" + body + ";";
		}
    };

	public void refresh() {
    	table.refresh();	
		changedDefinition();
	}

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
				if (dataProvider.isEditableNonscalarDefinition(rowPosition) && columnPosition == Attribute.HEADING_COLUMN)
					configLabels.addLabel("nonscalareditor");
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
		} else
			doDeleteSelected();
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
	
	protected abstract String getAttributeSource();
	
	public String getRelDefinition() {
		return dataProvider.getRelDefinition();
	}
	
}
