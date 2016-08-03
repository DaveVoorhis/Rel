package org.reldb.dbrowser.ui.content.rel;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
import org.reldb.dbrowser.ui.content.rel.var.VarRealCreator;
import org.reldb.dbrowser.ui.content.rel.var.VarRealDesigner;
import org.reldb.dbrowser.ui.content.rel.var.VarRealDropper;
import org.reldb.dbrowser.ui.content.rel.var.VarRealPlayer;
import org.reldb.dbrowser.ui.content.rel.view.VarViewCreator;
import org.reldb.dbrowser.ui.content.rel.view.VarViewDesigner;
import org.reldb.dbrowser.ui.content.rel.view.VarViewDropper;
import org.reldb.dbrowser.ui.content.rel.view.VarViewPlayer;
import org.reldb.dbrowser.ui.content.rel.welcome.WelcomeView;
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
		
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
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
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				DbTreeItem selection = getSelection();
				if (selection != null && selection.canPlay())
					playItem();
			}
		});
				
		Menu menu = new Menu(this);
		MenuItem showItem = new MenuItem(menu, SWT.POP_UP);
		showItem.setText("Show");
		showItem.setImage(IconLoader.loadIcon("play"));
		showItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				playItem();
			}
		});
		MenuItem createItem = new MenuItem(menu, SWT.POP_UP);
		createItem.setText("Create");
		createItem.setImage(IconLoader.loadIcon("item_add"));
		createItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				createItem();
			}
		});
		MenuItem dropItem = new MenuItem(menu, SWT.POP_UP);
		dropItem.setText("Drop");
		dropItem.setImage(IconLoader.loadIcon("item_delete"));
		dropItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				dropItem();
			}
		});
		MenuItem designItem = new MenuItem(menu, SWT.POP_UP);
		designItem.setText("Design");
		designItem.setImage(IconLoader.loadIcon("item_design"));
		designItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				designItem();
			}
		});
		MenuItem renameItem = new MenuItem(menu, SWT.POP_UP);
		renameItem.setText("Rename");
		renameItem.setImage(IconLoader.loadIcon("rename"));
		renameItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				renameItem();
			}
		});
		
		tree.setMenu(menu);
		
		tree.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				DbTreeItem selection = getSelection();
				if (selection == null) {
					showItem.setEnabled(false);
					createItem.setEnabled(false);
					dropItem.setEnabled(false);
					designItem.setEnabled(false);
					renameItem.setEnabled(false);
				} else {
					showItem.setEnabled(selection.canPlay());
					createItem.setEnabled(selection.canCreate());
					dropItem.setEnabled(selection.canDrop());
					designItem.setEnabled(selection.canDesign());
					renameItem.setEnabled(selection.canRename());
				}
			}
		});
		
		treeRoots = new HashMap<String, TreeItem>();
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER | SWT.CLOSE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				fireDbTreeTabchangeEvent();
			}
		});
		
		Menu tabControlMenu = new Menu(tabFolder);
		tabFolder.setMenu(tabControlMenu);
		MenuItem closer = new MenuItem(tabControlMenu, SWT.NONE);
		MenuItem closeOthers = new MenuItem(tabControlMenu, SWT.NONE);
		MenuItem closeLeft = new MenuItem(tabControlMenu, SWT.NONE);
		MenuItem closeRight = new MenuItem(tabControlMenu, SWT.NONE);
		new MenuItem(tabControlMenu, SWT.SEPARATOR);
		MenuItem closeAll = new MenuItem(tabControlMenu, SWT.NONE);
		
		closer.setText("Close");
		closeOthers.setText("Close others");
		closeLeft.setText("Close left tabs");
		closeRight.setText("Close right tabs");
		closeAll.setText("Close all");
		
		tabFolder.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event e) {
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
			}
		});
		
		closer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (itemSelectedByMenu != null)
					itemSelectedByMenu.dispose();
			}
		});
		closeOthers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.setSelection(itemSelectedByMenuIndex);
				for (CTabItem tab: tabFolder.getItems())
					if (tab != itemSelectedByMenu)
						tab.dispose();
			}
		});
		closeLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (itemSelectedByMenuIndex > 0) {
					Vector<CTabItem> closers = new Vector<CTabItem>();
					for (int i=0; i<itemSelectedByMenuIndex; i++)
						closers.add(tabFolder.getItem(i));
					tabFolder.setSelection(itemSelectedByMenuIndex);
					for (CTabItem close: closers)
						close.dispose();
				}
			}
		});
		closeRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (itemSelectedByMenuIndex < tabFolder.getItemCount() - 1) {
					Vector<CTabItem> closers = new Vector<CTabItem>();
					for (int i = itemSelectedByMenuIndex + 1; i<tabFolder.getItemCount(); i++)
						closers.add(tabFolder.getItem(i));
					tabFolder.setSelection(itemSelectedByMenuIndex);
					for (CTabItem close: closers)
						close.dispose();
				}
			}
		});
		closeAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				while (tabFolder.getItemCount() > 0)
					tabFolder.getItem(0).dispose();
			}
		});
		
		ToolBar zoomer = new ToolBar(tabFolder, SWT.NONE);
		ToolItem zoomItem = new ToolItem(zoomer, SWT.NONE);
		zoomItem.setImage(IconLoader.loadIcon("view_fullscreen"));
		zoomItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				zoom();
			}
		});
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
		TreeItem treeSelection = getTreeSelection();
		getSelection().play(treeSelection.getImage());
		nudge();
	}

	public void createItem() {
		getSelection().create(IconLoader.loadIcon("item_add"));
		nudge();
	}

	public void dropItem() {
		getSelection().drop(IconLoader.loadIcon("item_delete"));
		nudge();
	}

	public void designItem() {
		getSelection().design(IconLoader.loadIcon("item_design"));
		nudge();
	}

	protected void renameItem() {
		getSelection().rename(IconLoader.loadIcon("rename"));
		nudge();
	}

	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}

	private TreeItem getRoot(String section, Image image, DbTreeAction creator) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new TreeItem(tree, SWT.NONE);
			root.setImage(image);
			root.setText(section);
			treeRoots.put(section, root);
			root.setData(new DbTreeItem(section, null, creator, null, null, null));
		}
		return root;
	}
	
	private void buildSubtree(String section, Image image, String query, String displayAttributeName, Predicate<String> filter, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer) {
		TreeItem root = getRoot(section, image, creator);
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue(displayAttributeName).toString();
					if (filter.test(name)) {
						TreeItem item = new TreeItem(root, SWT.NONE);
						item.setImage(image);
						item.setText(name);
						item.setData(new DbTreeItem(section, player, creator, dropper, designer, renamer, name));
					}
				}
		}
	}
	
	private void buildSubtreeOperator(String whereSysStr, Predicate<String> filter) {
		String query = "EXTEND sys.Operators: {Impl := EXTEND Implementations " + whereSysStr + ": {SigReturn := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF}} {Name, Impl} ORDER (ASC Name)";
		OperatorCreator creator = new OperatorCreator(this);
		String section = CATEGORY_OPERATOR;
		Image image = IconLoader.loadIcon("flow_chart");
		TreeItem root = getRoot(section, image, creator);
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue("Name").toString();
					if (filter.test(name)) {
						TreeItem itemHeading = new TreeItem(root, SWT.NONE);
						itemHeading.setImage(image);
						itemHeading.setText(name);
						itemHeading.setData(new DbTreeItem(section, null, creator, null, null, null, name));
						int implementationCount = 0;
						String lastSignatureWithReturns = "";
						DbTreeItem lastitem = null;
						for (Tuple detailTuple: (Tuples)tuple.get("Impl")) {
							TreeItem item = new TreeItem(itemHeading, SWT.NONE);
							item.setImage(image);
							lastSignatureWithReturns = detailTuple.getAttributeValue("SigReturn").toString();
							lastitem = new DbTreeItem(section, 
									new OperatorPlayer(this), 
									creator, 
									new OperatorDropper(this), 
									new OperatorDesigner(this), 
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
	
	private void buildSubtree(String section, Image image, String query, String displayAttributeName, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer) {
		buildSubtree(section, image, query, displayAttributeName, (String attributeName) -> true, player, creator, dropper, designer, renamer);
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();
		
		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		Predicate<String> revSysNamesFilter = (String attributeName) -> attributeName.startsWith("sys.rev") ? showSystemObjects : true; 
		
		buildSubtree(CATEGORY_VARIABLE, IconLoader.loadIcon("table"), "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter, 
			new VarRealPlayer(this), new VarRealCreator(this), new VarRealDropper(this), new VarRealDesigner(this), null);
		
		buildSubtree(CATEGORY_VIEW, IconLoader.loadIcon("view"), "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter,
			new VarViewPlayer(this), new VarViewCreator(this), new VarViewDropper(this), new VarViewDesigner(this), null);
		
		buildSubtreeOperator(whereSysStr, revSysNamesFilter);
		
		buildSubtree(CATEGORY_TYPE, IconLoader.loadIcon("tau"), "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new TypePlayer(this), new TypeCreator(this), new TypeDropper(this), null, null);
		
		buildSubtree(CATEGORY_CONSTRAINT, IconLoader.loadIcon("constraint"), "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new ConstraintPlayer(this), new ConstraintCreator(this), new ConstraintDropper(this), new ConstraintDesigner(this), null);
		
		if (connection.hasRevExtensions() >= 0)
			handleRevAddition();
		
		buildSubtree(CATEGORY_WELCOME, IconLoader.loadIcon("smile"), "REL {TUP {Name 'Introduction'}}", "Name",
				new WelcomeView(this), null, null, null, null);
		
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
		buildSubtree(CATEGORY_QUERY, IconLoader.loadIcon("query"), "UNION {sys.rev.Query {model}, sys.rev.Relvar {model}}", "model",
				new QueryPlayer(this), new QueryCreator(this), new QueryDropper(this), new QueryDesigner(this), null);
		// buildSubtree("Forms", null, null, null, null, null, null);
		// buildSubtree("Reports", null, null, null, null, null, null);
		buildSubtree(CATEGORY_SCRIPT, IconLoader.loadIcon("script"), "sys.rev.Script {Name} ORDER (ASC Name)", "Name", 
			new ScriptPlayer(this), new ScriptCreator(this), new ScriptDropper(this), new ScriptDesigner(this), new ScriptRenamer(this));		
	}
	
	public void handleRevRemoval() {
		parentTab.refresh();
		removeSubtree(CATEGORY_QUERY);
		// removeSubtree("Forms");
		// removeSubtree("Reports");
		removeSubtree(CATEGORY_SCRIPT);
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
	
}
