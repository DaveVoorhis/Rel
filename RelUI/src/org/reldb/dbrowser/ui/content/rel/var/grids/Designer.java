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
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
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
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
					IEditableRule.ALWAYS_EDITABLE);
			// style for selected cells
			Style selectStyle = new Style();
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, selectStyle, DisplayMode.SELECT);
			// open adjacent editor when we leave the current one during editing
			configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
					DisplayMode.EDIT);
			// style for upper left corner
			BorderStyle borderStyle = new BorderStyle();
			borderStyle.setColor(GUIHelper.COLOR_GRAY);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
					new LineBorderDecorator(new TextPainter(), borderStyle), DisplayMode.NORMAL, GridRegion.CORNER);
			// for each column...
			for (int column = 0; column < headingProvider.getColumnCount(); column++)
				addColumn(column);
		}

		public void addColumn(int column) {
			String columnLabel = "column" + column;
			switch (column) {
			case Attr.NAME_COLUMN:
				registerAttributeNameColumn(registry, columnLabel);
				break;
			case Attr.TYPE_COLUMN:
				registerTypeNameColumn(registry, columnLabel);
				break;
			case Attr.HEADING_COLUMN:
				registerTypeDefinitionColumn(registry, columnLabel);
				break;
			default:
				registerKeyColumn(registry, columnLabel);
			}
		}

		private void registerAttributeNameColumn(IConfigRegistry configRegistry, String columnLabel) {
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
					columnLabel);
		}

		private void registerTypeNameColumn(IConfigRegistry configRegistry, String columnLabel) {
			Style cellStyle = new Style();
			cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
					columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.EDIT,
					columnLabel);
			// use a combobox
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new ComboBoxCellEditor(getTypes()),
					DisplayMode.EDIT, columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(),
					DisplayMode.EDIT, columnLabel);
		}

		private void registerTypeDefinitionColumn(IConfigRegistry configRegistry, String columnLabel) {
			// edit or not
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, new IEditableRule() {
				@Override
				public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry) {
					return isEditable(cell.getColumnIndex(), cell.getRowIndex());
				}

				@Override
				public boolean isEditable(int columnIndex, int rowIndex) {
					return dataProvider.isEditableNonscalarDefinition(rowIndex);
				}
			}, DisplayMode.EDIT, columnLabel);

			// Button displayed if editable
			ImagePainter imagePainter = new ImagePainter(IconLoader.loadIcon("table_design"));
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imagePainter, DisplayMode.NORMAL,
					"nonscalareditor");

			// Custom dialog box
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
					new AttributeDesignerCellEditor(Designer.this), DisplayMode.EDIT, columnLabel);
		}

		private void registerKeyColumn(IConfigRegistry configRegistry, String columnLabel) {
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
					DisplayMode.EDIT, columnLabel);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
					DisplayMode.NORMAL, columnLabel);
		}
	}

	class HeadingProvider implements IDataProvider {
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			switch (columnIndex) {
			case Attr.NAME_COLUMN:
				return "Name";
			case Attr.TYPE_COLUMN:
				return "Type";
			case Attr.HEADING_COLUMN:
				return "Heading";
			default:
				return (columnIndex == getColumnCount() - 1 && columnIndex > Attr.COLUMN_COUNT) ? "New Key"
						: "Key " + (columnIndex - (Attr.COLUMN_COUNT - 1));
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

	/**
	 * Override to be notified when the relvar definition has (probably) changed.
	 */
	protected void changedDefinition() {
	}

	class DataProvider implements IDataProvider {

		private Vector<Attr> attributes;
		private int lastColumnIndex = 0;
		private String kind;
		private TypeInfo typeInfo;

		private int getLastColumnIndex() {
			return Attr.COLUMN_COUNT + ((keys != null) ? keys.size() : 0) - 1;
		}

		public DataProvider() {
			typeInfo = new TypeInfo(connection);
			reload();
			lastColumnIndex = getLastColumnIndex();
		}

		private String getKind() {
			if (getAttributeSource().length() == 0)
				return null;
			return typeInfo.getKindFor(getAttributeSource());
		}

		// 1st column = attribute name; 2nd column = type name; 3rd column = TypeInfo
		private Tuples getAttributes() {
			if (getAttributeSource().length() == 0)
				return null;
			return typeInfo.getAttributesFor(getAttributeSource());
		}

		public void reload() {
			kind = getKind();
			attributes = new Vector<Attr>();
			Tuples tuples = getAttributes();
			if (tuples != null) {
				Iterator<Tuple> iterator = tuples.iterator();
				while (iterator.hasNext())
					attributes.add(new Attr(iterator.next()));
			}
			attributes.add(new Attr());
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (columnIndex < Attr.COLUMN_COUNT)
				return attributes.get(rowIndex).getColumnValue(columnIndex);
			else
				return keys.get(columnIndex - Attr.COLUMN_COUNT).contains(attributes.get(rowIndex).getName());
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			Attr attribute = attributes.get(rowIndex);
			if (columnIndex < Attr.COLUMN_COUNT) {
				if (newValue == null || newValue.toString().length() == 0)
					return;
				if (columnIndex == 0) {
					if (keys != null)
						for (HashSet<String> key : keys) {
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
					keys.get(columnIndex - Attr.COLUMN_COUNT).remove(attribute.getName());
				else
					keys.get(columnIndex - Attr.COLUMN_COUNT).add(attribute.getName());
			}
			if (columnIndex >= lastColumnIndex && newValue != null && newValue.equals("true")) {
				keys.add(new HashSet<String>());
				lastColumnIndex++;
				editorConfiguration.addColumn(columnIndex);
				table.configure();
				table.refresh();
			} else if (columnIndex >= Attr.COLUMN_COUNT && getKeyAttributeCount(columnIndex - Attr.COLUMN_COUNT) == 0) {
				keys.remove(columnIndex - Attr.COLUMN_COUNT);
				lastColumnIndex--;
				table.configure();
				table.refresh();
			}
			int lastRowIndex = attributes.size() - 1;
			if (rowIndex == lastRowIndex && attributes.get(lastRowIndex).isFilled()) {
				attributes.add(new Attr());
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
			return attributes.size();
		}

		public boolean isEditableNonscalarDefinition(int rowIndex) {
			return attributes.get(rowIndex).isEditableNonscalarDefinition();
		}

		public int getKeyAttributeCount(int keyColumn) {
			return keys.get(keyColumn).size();
		}

		public void deleteRows(Set<Range> selections) {
			HashSet<Integer> deleteMe = new HashSet<Integer>();
			for (Range range : selections)
				for (int n = range.start; n < range.end; n++)
					deleteMe.add(n);
			Vector<Attr> newCache = new Vector<Attr>();
			int index = 0;
			for (Attr attribute : attributes) {
				if (deleteMe.contains(index)) {
					if (keys != null)
						for (HashSet<String> key : keys)
							key.remove(attribute.getName());
				} else
					newCache.add(attribute);
				index++;
			}
			if (keys != null) {
				HashSet<HashSet<String>> newKeys = new HashSet<HashSet<String>>();
				for (HashSet<String> key : keys)
					if (key.size() > 0)
						newKeys.add(key);
				keys.clear();
				keys.addAll(newKeys);
				// Blank key definition allows user to add keys
				keys.add(new HashSet<String>());
				lastColumnIndex = getLastColumnIndex();
			}
			attributes = newCache;
			table.refresh();
			changedDefinition();
		}

		// Convert this into a TypeInfo literal
		public String getTypeInfoLiteral() {
			String body = "";
			for (Attr attribute : attributes)
				if (attribute.isFilled())
					body += ((body.length() > 0) ? "," : "") + "\n\t" + attribute.getTypeInfoLiteral();
			return "NonScalar(\"" + kind + "\", RELATION {AttrName CHARACTER, AttrType TypeInfo} {" + body + "})";
		}

		private String getRelKeysDefinition(Vector<HashSet<String>> keys) {
			String keysDef = "";
			if (keys.size() == 1)
				keysDef = "KEY {}";
			else
				for (int keyIndex = 0; keyIndex < keys.size() - 1; keyIndex++) {
					HashSet<String> key = keys.get(keyIndex);
					String keyDef = "";
					for (String keyAttribute : key) {
						keyDef += ((keyDef.length() > 0) ? ", " : "") + keyAttribute;
					}
					keysDef += ((keysDef.length() > 0) ? " " : "") + "KEY {" + keyDef + "}";
				}
			return keysDef;
		}

		private String getRelKeysDefinition() {
			return getRelKeysDefinition(keys);
		}

		private String getRelHeadingDefinition(Attr a) {
			return typeInfo.getHeadingDefinition(a.getNewColumnValue(Attr.HEADING_COLUMN));
		}

		private String getRelAlterClause(Attr a) {
			if (a.isNameChange() && a.isHeadingChange()) {
				return "DROP " + a.getOriginalColumnValue(Attr.NAME_COLUMN) + "\n\t" + "INSERT " + a.getName() + " "
						+ getRelHeadingDefinition(a);
			} else if (a.isNameChange() && a.isTypeNameChange() && !a.isHeadingChange()) {
				return "DROP " + a.getOriginalColumnValue(Attr.NAME_COLUMN) + "\n\t" + "INSERT " + a.getName() + " "
						+ a.getNewColumnValue(Attr.TYPE_COLUMN);
			} else if (a.isNameChange() && !a.isTypeNameChange() && !a.isHeadingChange()) {
				return "RENAME " + a.getOriginalColumnValue(Attr.NAME_COLUMN) + " TO " + a.getName();
			} else if (!a.isNameChange() && a.isHeadingChange()) {
				return "TYPE_OF " + a.getOriginalColumnValue(Attr.NAME_COLUMN) + " TO " + getRelHeadingDefinition(a);
			} else if (!a.isNameChange() && a.isTypeNameChange() && !a.isHeadingChange()) {
				return "TYPE_OF " + a.getOriginalColumnValue(Attr.NAME_COLUMN) + " TO "
						+ a.getNewColumnValue(Attr.TYPE_COLUMN);
			} else
				return null;
		}

		private String getRelAddClause(Attr a) {
			return "INSERT " + a.getName() + " " + (a.isEditableNonscalarDefinition() ? getRelHeadingDefinition(a)
					: a.getColumnValue(Attr.TYPE_COLUMN));
		}

		public String getRelDefinition() {
			// attributes
			Tuples existingDefinitionTuples = getAttributes();
			HashMap<String, Attr> existingDefinition = new HashMap<String, Attr>();
			if (existingDefinitionTuples != null) {
				Iterator<Tuple> iterator = existingDefinitionTuples.iterator();
				while (iterator.hasNext()) {
					Attr attribute = new Attr(iterator.next());
					existingDefinition.put(attribute.getName(), attribute);
				}
			}
			String body = "";
			for (Attr attribute : attributes) {
				if (!attribute.isFilled())
					continue;
				String originalAttributeName = attribute.getOriginalColumnValue(Attr.NAME_COLUMN);
				if (originalAttributeName != null && existingDefinition.containsKey(originalAttributeName)) {
					String alterClause = getRelAlterClause(attribute);
					if (alterClause != null)
						body += ((body.length() > 0) ? "\n" : "") + "\t" + alterClause;
					existingDefinition.remove(originalAttributeName);
				} else
					body += ((body.length() > 0) ? "\n" : "") + "\t" + getRelAddClause(attribute);
			}
			for (String attributeName : existingDefinition.keySet())
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
			return "ALTER VAR " + relvarName + "\n" + body + ";";
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

		gridLayer = new DefaultGridLayer(dataProvider, headingProvider);

		// CellLabelAccumulator determines how cells will be displayed
		class CellLabelAccumulator implements IConfigLabelAccumulator {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
				configLabels.addLabel("column" + columnPosition);
				if (dataProvider.isEditableNonscalarDefinition(rowPosition) && columnPosition == Attr.HEADING_COLUMN)
					configLabels.addLabel("nonscalareditor");
			}
		}

		DataLayer bodyDataLayer = (DataLayer) gridLayer.getBodyDataLayer();
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
				doesDelete.setImage(IconLoader.loadIcon("table_row_delete"));
				doesDelete.addListener(SWT.Selection, e -> askDeleteSelected());
			}
		};
		table.addConfiguration(new MenuConfiguration(GridRegion.ROW_HEADER,
				new PopupMenuBuilder(table).withContributionItem(rowMenuItems)));

		// Tabbing wraps and moves up/down
		gridLayer.registerCommandHandler(new MoveCellSelectionCommandHandler(
				gridLayer.getBodyLayer().getSelectionLayer(), ITraversalStrategy.TABLE_CYCLE_TRAVERSAL_STRATEGY));

		table.configure();

		// Tooltip for row/column headings
		new NatTableContentTooltip(table, GridRegion.ROW_HEADER) {
			protected String getText(Event event) {
				return "Right-click for options.";
			}
		};
	}

	private void doDeleteSelected() {
		Set<Range> selections = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowPositions();
		dataProvider.deleteRows(selections);
	}

	public void askDeleteSelected() {
		if (askDeleteConfirm) {
			int selectedRowCount = gridLayer.getBodyLayer().getSelectionLayer().getSelectedRowCount();
			DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(table.getShell(), selectedRowCount,
					"attribute");
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
		for (Tuple typeName : typeNames)
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
