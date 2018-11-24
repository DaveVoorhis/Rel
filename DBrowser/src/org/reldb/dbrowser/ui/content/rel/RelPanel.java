package org.reldb.dbrowser.ui.content.rel;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.IconMenuItem;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.constraint.ConstraintCreator;
import org.reldb.dbrowser.ui.content.rel.constraint.ConstraintDesigner;
import org.reldb.dbrowser.ui.content.rel.constraint.ConstraintDropper;
import org.reldb.dbrowser.ui.content.rel.constraint.ConstraintPlayer;
import org.reldb.dbrowser.ui.content.rel.operator.OperatorCreator;
import org.reldb.dbrowser.ui.content.rel.operator.OperatorDesigner;
import org.reldb.dbrowser.ui.content.rel.operator.OperatorDropper;
import org.reldb.dbrowser.ui.content.rel.operator.OperatorPlayer;
import org.reldb.dbrowser.ui.content.rel.query.QueryCreator;
import org.reldb.dbrowser.ui.content.rel.query.QueryDesigner;
import org.reldb.dbrowser.ui.content.rel.query.QueryDropper;
import org.reldb.dbrowser.ui.content.rel.query.QueryPlayer;
import org.reldb.dbrowser.ui.content.rel.script.ScriptCreator;
import org.reldb.dbrowser.ui.content.rel.script.ScriptDesigner;
import org.reldb.dbrowser.ui.content.rel.script.ScriptDropper;
import org.reldb.dbrowser.ui.content.rel.script.ScriptPlayer;
import org.reldb.dbrowser.ui.content.rel.script.ScriptRenamer;
import org.reldb.dbrowser.ui.content.rel.type.TypeCreator;
import org.reldb.dbrowser.ui.content.rel.type.TypeDropper;
import org.reldb.dbrowser.ui.content.rel.type.TypePlayer;
import org.reldb.dbrowser.ui.content.rel.var.VarCreator;
import org.reldb.dbrowser.ui.content.rel.var.VarDesigner;
import org.reldb.dbrowser.ui.content.rel.var.VarDropper;
import org.reldb.dbrowser.ui.content.rel.var.VarEditor;
import org.reldb.dbrowser.ui.content.rel.var.VarExporter;
import org.reldb.dbrowser.ui.content.rel.var.VarPlayer;
import org.reldb.dbrowser.ui.content.rel.view.VarViewCreator;
import org.reldb.dbrowser.ui.content.rel.view.VarViewDesigner;
import org.reldb.dbrowser.ui.content.rel.view.VarViewDropper;
import org.reldb.dbrowser.ui.content.rel.view.VarViewExporter;
import org.reldb.dbrowser.ui.content.rel.view.VarViewPlayer;
import org.reldb.dbrowser.ui.content.rel.welcome.WelcomeView;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class RelPanel extends Composite {
	
	public final static String CATEGORY_VARIABLE = "Variable";
	public final static String CATEGORY_VIEW = "View";
	public final static String CATEGORY_OPERATOR = "Operator";
	public final static String CATEGORY_TYPE = "Type";
	public final static String CATEGORY_CONSTRAINT = "Constraint";
	public final static String CATEGORY_SCRIPT = "Script";
	public final static String CATEGORY_QUERY = "Query";
	public final static String CATEGORY_WELCOME = "Welcome";
		
	private DbTab parentTab;
	
	private DbConnection connection;
	private CrashHandler crashHandler;
	private boolean showSystemObjects = false;
	
	private SashForm sashForm;
	
	private CTabFolder tabFolder;
	private CTabFolder treeFolder;
	
	private Tree tree;
	private HashMap<String, TreeItem> treeRoots;
	
	private CTabItem itemSelectedByMenu;
	private int itemSelectedByMenuIndex;
	private PreferenceChangeListener preferenceChangeListener;
	
	private static class IconTreeItem extends TreeItem {
		private String imageName;
		
		public IconTreeItem(Tree tree, String imageName, int style) {
			super(tree, style);
			this.imageName = imageName;
			if (imageName != null)
				setImage(IconLoader.loadIcon(imageName));
		}
		
		public IconTreeItem(TreeItem treeItem, String iconName, int style) {
			super(treeItem, style);
			this.imageName = iconName;
			if (iconName != null)
				setImage(IconLoader.loadIcon(iconName));
		}
		
		public String getImageName() {
			return imageName;
		}

		public void reloadImage() {
			if (imageName != null)
				setImage(IconLoader.loadIcon(imageName));
		}
		
		public void checkSubclass() {}
	}
	
	private void refreshIcons(TreeItem tree[]) {
		for (TreeItem treeItem: tree) {
			if (treeItem instanceof IconTreeItem)
				((IconTreeItem)treeItem).reloadImage();
			refreshIcons(treeItem.getItems());
		}
	}
	
	/**
	 * Create the composite.
	 * @param parentTab 
	 * @param parent
	 * @param style
	 */
	public RelPanel(DbTab parentTab, Composite parent, int style) {
		super(parent, style);
		this.parentTab = parentTab;
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		connection = parentTab.getConnection();
		crashHandler = parentTab.getCrashHandler();
		
		sashForm = new SashForm(this, SWT.NONE);
	
		treeFolder = new CTabFolder(sashForm, SWT.BORDER);
		CTabItem treeTab = new CTabItem(treeFolder, SWT.NONE);
		treeTab.setText("Database");
		treeTab.setImage(IconLoader.loadIcon("DatabaseIcon"));
		tree = new Tree(treeFolder, SWT.NONE);		
		treeTab.setControl(tree);
		treeFolder.setSelection(0);

		preferenceChangeListener = new PreferenceChangeAdapter("RelPanel(Tree)") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				refreshIcons(tree.getItems());
				treeTab.setImage(IconLoader.loadIcon("DatabaseIcon"));
				getShell().layout(new Control[] {RelPanel.this, tree}, SWT.DEFER);
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
	
		tree.addListener(SWT.Selection, evt -> {
			DbTreeItem selection = getSelection();
			if (selection == null)
				fireDbTreeNoSelectionEvent();
			else {
				fireDbTreeSelectionEvent(selection);
				String name = selection.getTabName();
				CTabItem tab = getTab(name);
				if (tab != null) {
					getTabFolder().setSelection(tab);
					fireDbTreeTabchangeEvent();
				}
			}
		});
		
		tree.addListener(SWT.KeyUp, evt -> {
			if (evt.character == 13) {
				TreeItem items[] = tree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
				DbTreeItem selection = getSelection();
				if (selection != null && selection.canPlay())
					playItem();
			}
		});
		
		tree.addListener(SWT.MouseDoubleClick, evt -> {
			String osName = System.getProperty("os.name").toLowerCase();
			if (!osName.startsWith("win")) {		// don't do this under Windows!
				TreeItem items[] = tree.getSelection();
				for (TreeItem item: items)
					item.setExpanded(!item.getExpanded());
			}
			DbTreeItem selection = getSelection();
			if (selection != null && selection.canPlay())
				playItem();
		});

		Menu menu = new Menu(this);
		
		IconMenuItem showItem = new IconMenuItem(menu, "Show", "play", SWT.PUSH, e -> playItem());
		IconMenuItem editItem = new IconMenuItem(menu, "Edit", "item_edit", SWT.PUSH, e -> editItem());
		IconMenuItem createItem = new IconMenuItem(menu, "Create", "item_add", SWT.PUSH, e -> createItem());
		IconMenuItem dropItem = new IconMenuItem(menu, "Drop", "item_delete", SWT.PUSH, e -> dropItem());
		IconMenuItem designItem = new IconMenuItem(menu, "Design", "item_design", SWT.PUSH, e -> designItem());
		IconMenuItem renameItem = new IconMenuItem(menu, "Rename", "rename", SWT.PUSH, e -> renameItem());
		IconMenuItem exportItem = new IconMenuItem(menu, "Export", "export", SWT.PUSH, e -> exportItem());
		
		tree.setMenu(menu);
		
		tree.addMenuDetectListener(e -> {
			DbTreeItem selection = getSelection();
			if (selection == null) {
				showItem.setEnabled(false);
				editItem.setEnabled(false);
				createItem.setEnabled(false);
				dropItem.setEnabled(false);
				designItem.setEnabled(false);
				renameItem.setEnabled(false);
				exportItem.setEnabled(false);
			} else {
				showItem.setEnabled(selection.canPlay());
				editItem.setEnabled(selection.canEdit());
				createItem.setEnabled(selection.canCreate());
				dropItem.setEnabled(selection.canDrop());
				designItem.setEnabled(selection.canDesign());
				renameItem.setEnabled(selection.canRename());
				exportItem.setEnabled(selection.canExport());
			}
		});
		
		treeRoots = new HashMap<String, TreeItem>();
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER | SWT.CLOSE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addListener(SWT.Selection, e -> fireDbTreeTabchangeEvent());
		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void close(CTabFolderEvent event) {
				if (!((DbTreeTab)event.item).canClose()) {
					event.doit = false;
					return;
				}
				if (tabFolder.getItemCount() <= 1)
					unzoom();
			}
		});
		
		Menu tabControlMenu = new Menu(tabFolder);
		tabFolder.setMenu(tabControlMenu);
		
		IconMenuItem closer = new IconMenuItem(tabControlMenu, "Close", null, SWT.NONE, e -> {
			if (itemSelectedByMenu != null)
				itemSelectedByMenu.dispose();
		});
		IconMenuItem closeOthers = new IconMenuItem(tabControlMenu, "Close others", null, SWT.NONE, e -> {
			tabFolder.setSelection(itemSelectedByMenuIndex);
			for (CTabItem tab: tabFolder.getItems())
				if (tab != itemSelectedByMenu)
					tab.dispose();
		});
		IconMenuItem closeLeft = new IconMenuItem(tabControlMenu, "Close left tabs", null, SWT.NONE, e -> {
			if (itemSelectedByMenuIndex > 0) {
				Vector<CTabItem> closers = new Vector<CTabItem>();
				for (int i=0; i<itemSelectedByMenuIndex; i++)
					closers.add(tabFolder.getItem(i));
				tabFolder.setSelection(itemSelectedByMenuIndex);
				for (CTabItem close: closers)
					close.dispose();
			}
		});
		IconMenuItem closeRight = new IconMenuItem(tabControlMenu, "Close right tabs", null, SWT.NONE, e -> {
			if (itemSelectedByMenuIndex < tabFolder.getItemCount() - 1) {
				Vector<CTabItem> closers = new Vector<CTabItem>();
				for (int i = itemSelectedByMenuIndex + 1; i<tabFolder.getItemCount(); i++)
					closers.add(tabFolder.getItem(i));
				tabFolder.setSelection(itemSelectedByMenuIndex);
				for (CTabItem close: closers)
					close.dispose();
			}
		});
		new MenuItem(tabControlMenu, SWT.SEPARATOR);
		IconMenuItem closeAll = new IconMenuItem(tabControlMenu, "Close all", null, SWT.NONE, e -> {
			while (tabFolder.getItemCount() > 0)
				tabFolder.getItem(0).dispose();
		});
		
		tabFolder.addListener(SWT.MenuDetect, e -> {
			Point clickPosition = Display.getDefault().map(null, tabFolder, new Point(e.x, e.y));
			if (clickPosition.y > tabFolder.getTabHeight())
				e.doit = false;
			else {
				itemSelectedByMenu = tabFolder.getItem(clickPosition);
				itemSelectedByMenuIndex = getTabIndex(tabFolder, itemSelectedByMenu);
				closer.setEnabled(itemSelectedByMenuIndex >= 0);
				closeOthers.setEnabled(tabFolder.getItemCount() > 1 && itemSelectedByMenuIndex >= 0);
				closeLeft.setEnabled(itemSelectedByMenuIndex > 0);
				closeRight.setEnabled(itemSelectedByMenuIndex >= 0 && itemSelectedByMenuIndex < tabFolder.getItemCount() - 1);
				closeAll.setEnabled(tabFolder.getItemCount() > 0);
			}
		});
		
		ManagedToolbar zoomer = new ManagedToolbar(tabFolder);
		new CommandActivator(null, zoomer, "view_fullscreen", SWT.NONE, "Zoom in or out", e -> zoom());
		tabFolder.setTopRight(zoomer);
		
		sashForm.setWeights(new int[] {1, 4});
		
		buildDbTree();
		
		boolean displayWelcome = true;
		if (connection.hasRevExtensions() >= 0) {
			RevDatabase db = new RevDatabase(connection);
			if (db.getSetting(getClass().getName() + "-showWelcome").equals("no"))
				displayWelcome = false;
		}
		
		if (displayWelcome) {
			TreeItem lastItem = tree.getItem(tree.getItemCount() - 1);
			lastItem.setExpanded(true);
			if (lastItem.getItemCount() > 0) {
				lastItem = lastItem.getItem(lastItem.getItemCount() - 1);
				tree.setSelection(lastItem);
				playItem();
				fireDbTreeTabchangeEvent();
			}
		}
		
		if (tabFolder.getItemCount() > 0)
			tabFolder.setFocus();
		else {
			tree.setSelection(tree.getItem(0));
			tree.setFocus();
		}
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);
		super.dispose();		
	}
	
	private void focusOnSelectedTab() {
		CTabItem selectedItem = tabFolder.getSelection();
		if (selectedItem != null) {
			Control tabControl = selectedItem.getControl();
			if (tabControl != null && !tabControl.isDisposed())
				tabControl.setFocus();
		}
	}

	public DbTab getDbTab() {
		return parentTab;
	}
	
	/** Invoke to force reload of toolbar. */
	public void changeToolbar() {}
	
	private static int getTabIndex(CTabFolder tabFolder, CTabItem item) {
		if (item == null)
			return -1;
		for (int index = 0; index < tabFolder.getItemCount(); index++)
			if (tabFolder.getItem(index) == item)
				return index;
		return -1;
	}
	
	public void notifyTabCreated() {
		fireDbTreeTabchangeEvent();
	}

	public DbConnection getConnection() {
		return connection;
	}

	public CrashHandler getCrashHandler() {
		return crashHandler;
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}
	
	public CTabItem getTab(String name) {
		for (CTabItem tab: tabFolder.getItems())
			if (tab.getText().equals(name))
				return tab;
		return null;
	}
	
	public CTabItem getTab(DbTreeItem item) {
		return getTab(item.getTabName());
	}
	
	private TreeItem getTreeSelection() {
		TreeItem items[] = tree.getSelection();
		if (items == null || items.length == 0)
			return null;
		return items[0];		
	}
	
	private DbTreeItem getSelection() {
		TreeItem treeItem = getTreeSelection();
		if (treeItem == null)
			return null;
		return (DbTreeItem)treeItem.getData();
	}
	
	private Vector<DbTreeListener> listeners = new Vector<DbTreeListener>();
	
	public void addDbTreeListener(DbTreeListener listener) {
		listeners.add(listener);
	}
	
	public void removeDbTreeListener(DbTreeListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireDbTreeTabchangeEvent() {
		for (DbTreeListener listener: listeners)
			listener.tabChangeNotify();
	}
	
	protected void fireDbTreeSelectionEvent(DbTreeItem item) {
		for (DbTreeListener listener: listeners)
			listener.select(item);
	}
	
	private void fireDbTreeNoSelectionEvent() {
		fireDbTreeSelectionEvent(new DbTreeItem());
	}
	
	// Invoked to force refresh of this panel. Required by GTK (apparently...) due to
	// failure to display widgets correctly without it.
	private void nudge() {
		this.setSize(getSize().x + 1, getSize().y);
		this.setSize(getSize().x - 1, getSize().y);		
	}

	private DbTreeTab getTab(String category, String text) {
		CTabItem tab = getTab(category + ": " + text);
		if (tab instanceof DbTreeTab)
			return (DbTreeTab)tab;
		return null;
	}

	private TreeItem getTreeItemRecursive(TreeItem item, String text) {
		if (item == null)
			return null;
		if (item.getText().equals(text))
			return item;		
		for (TreeItem subtreeItem: item.getItems()) {
			TreeItem result = getTreeItemRecursive(subtreeItem, text);
			if (result != null)
				return result;
		}
		return null;		
	}
	
	private TreeItem getTreeItem(String category, String text) {
		DbTreeTab tab = getTab(category, text);
		if (tab != null)
			tab.reload();
		TreeItem item = treeRoots.get(category);
		return getTreeItemRecursive(item, text);
	}
	
	private boolean selectItem(String category, String text) {
		TreeItem item = getTreeItem(category, text);
		if (item != null) {
			tree.setSelection(item);
			return true;
		}
		return false;
	}
	
	private void setTopItem(String category, String text) {
		TreeItem item = getTreeItem(category, text);
		if (item != null)
			tree.setTopItem(item);
	}
	
	public void openTabForDesign(String category, String text) {
		if (selectItem(category, text))
			designItem();
	}
	
	public void playItem() {
		IconTreeItem treeSelection = (IconTreeItem)getTreeSelection();
		getSelection().play(treeSelection.getImageName());
		nudge();
	}

	public void editItem() {
		getSelection().edit("item_edit");
		nudge();
	}
	
	public void createItem() {
		getSelection().create("item_add");
		nudge();
	}

	public void dropItem() {
		getSelection().drop("item_delete");
		nudge();
	}

	public void designItem() {
		getSelection().design("item_design");
		nudge();
	}

	protected void renameItem() {
		getSelection().rename("rename");
		nudge();
	}

	protected void exportItem() {
		getSelection().export("export");
		nudge();
	}
	
	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}

	private TreeItem getRoot(String section, String imageName, DbTreeAction creator) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new IconTreeItem(tree, imageName, SWT.NONE);
			root.setText(section);
			treeRoots.put(section, root);
			root.setData(new DbTreeItem(section, null, null, creator, null, null, null, null));
		}
		return root;
	}
	
	private void buildSubtree(String section, String imageName, String query, String displayAttributeName, Predicate<String> filter, DbTreeAction player, DbTreeAction editor, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter) {
		TreeItem root = getRoot(section, imageName, creator);
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue(displayAttributeName).toString();
					if (filter.test(name)) {
						TreeItem item = new IconTreeItem(root, imageName, SWT.NONE);
						item.setText(name);
						item.setData(new DbTreeItem(section, player, editor, creator, dropper, designer, renamer, exporter, name));
					}
				}
		}
	}
	
	private void buildSubtreeVar(String andSysStr, Predicate<String> filter) {
		String section = CATEGORY_VARIABLE;
		String query = "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name, isExternal} ORDER (ASC Name)";
		String displayAttributeName = "Name";
		VarPlayer player = new VarPlayer(this);
		VarEditor editor = new VarEditor(this);
		VarCreator creator = new VarCreator(this);
		VarDropper dropper = new VarDropper(this);
		VarDesigner designer = new VarDesigner(this);
		VarExporter exporter = new VarExporter(this);
		
		TreeItem root = getRoot(section, "table", creator);
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue(displayAttributeName).toString();
					if (filter.test(name)) {
						TreeItem item;
						if (tuple.getAttributeValue("isExternal").toBoolean())
							item = new IconTreeItem(root, "table_external", SWT.NONE);
						else
							item = new IconTreeItem(root, "table", SWT.NONE);
						item.setText(name);
						item.setData(new DbTreeItem(section, player, editor, creator, dropper, designer, null, exporter, name));
					}
				}
		}		
	}
	
	private void buildSubtreeOperator(String whereSysStr, Predicate<String> filter) {
		String query =
			"EXTEND " +
			"  UNION {" +
			"	 ((sys.Operators UNGROUP Implementations) " + whereSysStr + ") {Name, Signature, ReturnsType, Definition}," +
			"	 ((EXTEND sys.OperatorsBuiltin: {Owner := 'Rel'}) " + whereSysStr + ") {ALL BUT Owner}" +
			"  }" +
			": {SigReturn := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF}" +
			"GROUP {ALL BUT Name} AS Impl " +
			"ORDER (ASC Name)";
		OperatorCreator creator = new OperatorCreator(this);
		String section = CATEGORY_OPERATOR;
		TreeItem root = getRoot(section, "flow_chart", creator);
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue("Name").toString();
					if (filter.test(name)) {
						TreeItem itemHeading = new IconTreeItem(root, "flow_chart", SWT.NONE);
						itemHeading.setText(name);
						itemHeading.setData(new DbTreeItem(section, null, null, creator, null, null, null, null, name));
						int implementationCount = 0;
						String lastSignatureWithReturns = "";
						DbTreeItem lastitem = null;
						for (Tuple detailTuple: (Tuples)tuple.get("Impl")) {
							TreeItem item = new IconTreeItem(itemHeading, "flow_chart", SWT.NONE);
							lastSignatureWithReturns = detailTuple.getAttributeValue("SigReturn").toString();
							lastitem = new DbTreeItem(section, 
									new OperatorPlayer(this), 
									null,
									creator, 
									new OperatorDropper(this), 
									new OperatorDesigner(this), 
									null, 
									null,
									detailTuple.getAttributeValue("Signature").toString());
							item.setText(lastSignatureWithReturns);
							item.setData(lastitem);
							implementationCount++;
						}
						if (implementationCount == 0)
							itemHeading.dispose();
						else if (implementationCount == 1) {
							itemHeading.removeAll();
							itemHeading.setText(lastSignatureWithReturns);
							itemHeading.setData(lastitem);
						}
					}
				}
		}
	}
	
	private void removeSubtree(String section) {
		TreeItem root = treeRoots.get(section);
		if (root != null)
			treeRoots.remove(section);
		root.dispose();
	}
	
	private void buildSubtree(String section, String imageName, String query, String displayAttributeName, DbTreeAction player, DbTreeAction editor, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter) {
		buildSubtree(section, imageName, query, displayAttributeName, (String attributeName) -> true, player, editor, creator, dropper, designer, renamer, exporter);
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();
		
		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		Predicate<String> revSysNamesFilter = (String attributeName) -> attributeName.startsWith("sys.rev") ? showSystemObjects : true; 
		
		buildSubtreeVar(andSysStr, revSysNamesFilter);
		
		buildSubtree(CATEGORY_VIEW, "view", "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter,
			new VarViewPlayer(this), null, new VarViewCreator(this), new VarViewDropper(this), new VarViewDesigner(this), null, new VarViewExporter(this));
		
		buildSubtreeOperator(whereSysStr, revSysNamesFilter);
		
		buildSubtree(CATEGORY_TYPE, "tau", "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new TypePlayer(this), null, new TypeCreator(this), new TypeDropper(this), null, null, null);
		
		buildSubtree(CATEGORY_CONSTRAINT, "constraint", "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new ConstraintPlayer(this), null, new ConstraintCreator(this), new ConstraintDropper(this), new ConstraintDesigner(this), null, null);
		
		if (connection.hasRevExtensions() >= 0)
			handleRevAddition();
		
		buildSubtree(CATEGORY_WELCOME, "smile", "REL {TUP {Name 'Introduction'}}", "Name",
				new WelcomeView(this), null, null, null, null, null, null);
		
		fireDbTreeNoSelectionEvent();
	}
	
	public void redisplayed() {
		TreeItem[] selections = tree.getSelection();
		String selectedSection = null;
		String selectedText = null;
		if (selections != null && selections.length > 0) {
			DbTreeItem item = (DbTreeItem)selections[0].getData();
			if (item != null) {
				selectedSection = item.getSection();
				selectedText = selections[0].getText();
			}
		}
		TreeItem topItem = tree.getTopItem();
		String topSection = null;
		String topText = null;
		if (topItem != null) {
			DbTreeItem item = (DbTreeItem)topItem.getData();
			if (item != null) {
				topSection = item.getSection();
				topText = topItem.getText();
			}			
		}
		buildDbTree();
		if (selectedSection != null)
			selectItem(selectedSection, selectedText);
		if (topSection != null)
			setTopItem(topSection, topText);
	}

	public void handleRevAddition() {
		parentTab.refresh();
		buildSubtree(CATEGORY_QUERY, "query", "UNION {sys.rev.Query {model}, sys.rev.Relvar {model}}", "model",
				new QueryPlayer(this), null, new QueryCreator(this), new QueryDropper(this), new QueryDesigner(this), null, null);
		// buildSubtree("Forms", null, null, null, null, null, null, null);
		// buildSubtree("Reports", null, null, null, null, null, null, null);
		buildSubtree(CATEGORY_SCRIPT, "script", "sys.rev.Script {Name} ORDER (ASC Name)", "Name", 
			new ScriptPlayer(this), null, new ScriptCreator(this), new ScriptDropper(this), new ScriptDesigner(this), new ScriptRenamer(this), null);		
	}
	
	public void handleRevRemoval() {
		parentTab.refresh();
		removeSubtree(CATEGORY_QUERY);
		// removeSubtree("Forms");
		// removeSubtree("Reports");
		removeSubtree(CATEGORY_SCRIPT);
	}

	private void unzoom() {
		sashForm.setMaximizedControl(null);
	}
	
	private void zoomMain() {
		if (sashForm.getMaximizedControl() == null)
			sashForm.setMaximizedControl(tabFolder);
		else
			sashForm.setMaximizedControl(null);
	}
	
	public void zoom() {
		if (tabFolder.getItemCount() == 0)
			return;
		CTabItem tabItem = tabFolder.getSelection();
		if (tabItem != null && tabItem instanceof DbTreeTab) {
			DbTreeTab currentTab = (DbTreeTab)tabItem;
			if (currentTab.isSelfZoomable()) {
				currentTab.zoom();
				return;
			}
		}
		zoomMain();
	}

	public void switchToCmdMode() {
		parentTab.switchToCmdMode();
	}

	public void setTab(CTabItem tab) {
		getTabFolder().setSelection(tab);
		focusOnSelectedTab();
	}

	public void setTab(CTabItem tab, String imageName) {
		((DbTreeTab)tab).setImageName(imageName);
		setTab(tab);
	}
	
}
