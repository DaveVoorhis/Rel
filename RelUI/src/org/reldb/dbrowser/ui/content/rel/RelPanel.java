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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
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
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class RelPanel extends Composite {
	
	private DbConnection connection;
	private CrashHandler crashHandler;
	private boolean showSystemObjects = false;

	private CTabFolder tabFolder;
	
	private Tree tree;
	private HashMap<String, TreeItem> treeRoots;
	
	/**
	 * Create the composite.
	 * @param parentTab 
	 * @param parent
	 * @param style
	 */
	public RelPanel(DbTab parentTab, Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		connection = parentTab.getConnection();
		crashHandler = parentTab.getCrashHandler();
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		tree = new Tree(sashForm, SWT.NONE);
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

		tree.setMenu(menu);
		
		tree.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				DbTreeItem selection = getSelection();
				if (selection == null) {
					showItem.setEnabled(false);
					createItem.setEnabled(false);
					dropItem.setEnabled(false);
					designItem.setEnabled(false);
				} else {
					showItem.setEnabled(selection.canPlay());
					createItem.setEnabled(selection.canCreate());
					dropItem.setEnabled(selection.canDrop());
					designItem.setEnabled(selection.canDesign());
				}
			}
		});
		
		treeRoots = new HashMap<String, TreeItem>();
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER | SWT.CLOSE);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				fireDbTreeTabchangeEvent();
			}
		});
		
		sashForm.setWeights(new int[] {1, 4});
		
		buildDbTree();
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
	
	protected void fireDbTreeNoSelectionEvent() {
		fireDbTreeSelectionEvent(new DbTreeItem());
	}
	
	public void playItem() {
		TreeItem treeSelection = getTreeSelection();
		getSelection().play(treeSelection.getImage());
	}

	public void createItem() {
		getSelection().create(IconLoader.loadIcon("item_add"));
	}

	public void dropItem() {
		getSelection().drop(IconLoader.loadIcon("item_delete"));
	}

	public void designItem() {
		getSelection().design(IconLoader.loadIcon("item_design"));
	}

	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}
	
	private void buildSubtree(String section, Image image, String query, String displayAttributeName, Predicate<String> filter, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new TreeItem(tree, SWT.NONE);
			root.setImage(image);
			root.setText(section);
			treeRoots.put(section, root);
			root.setData(new DbTreeItem(section, null, creator, null, null));
		}
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue(displayAttributeName).toString();
					if (filter.test(name)) {
						TreeItem item = new TreeItem(root, SWT.NONE);
						item.setImage(image);
						item.setText(name);
						item.setData(new DbTreeItem(section, player, creator, dropper, designer, name));
					}
				}
		}
	}

	private void buildSubtree(String section, Image image, String query, String displayAttributeName, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer) {
		buildSubtree(section, image, query, displayAttributeName, (String attributeName) -> true, player, creator, dropper, designer);
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();

		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		Predicate<String> revSysNamesFilter = (String attributeName) -> attributeName.startsWith("sys.rev") ? showSystemObjects : true; 
		
		buildSubtree("Variable", IconLoader.loadIcon("table"), "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter, 
			new VarRealPlayer(this), new VarRealCreator(this), new VarRealDropper(this), new VarRealDesigner(this));
		
		buildSubtree("View", IconLoader.loadIcon("view"), "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter,
			new VarViewPlayer(this), new VarViewCreator(this), new VarViewDropper(this), new VarViewDesigner(this));
		
		buildSubtree("Operator", IconLoader.loadIcon("operator"), "EXTEND (sys.Operators UNGROUP Implementations)" + whereSysStr + ": {opName := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF} {opName} ORDER (ASC opName)", "opName",
			new OperatorPlayer(this), new OperatorCreator(this), new OperatorDropper(this), new OperatorDesigner(this));
		
		buildSubtree("Type", IconLoader.loadIcon("type"), "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new TypePlayer(this), new TypeCreator(this), new TypeDropper(this), null);
		
		buildSubtree("Constraint", IconLoader.loadIcon("constraint"), "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			new ConstraintPlayer(this), new ConstraintCreator(this), new ConstraintDropper(this), new ConstraintDesigner(this));
		
		if (connection.hasRevExtensions() >= 0) {
			buildSubtree("Query", IconLoader.loadIcon("query"), "UNION {sys.rev.Query {model}, sys.rev.Relvar {model}}", "model",
					new QueryPlayer(this), new QueryCreator(this), new QueryDropper(this), new QueryDesigner(this));
			// buildSubtree("Forms", null, null, null, null, null, null);
			// buildSubtree("Reports", null, null, null, null, null, null);
			// buildSubtree("Scripts", null, null, null, null, null, null);
		}
		
		fireDbTreeNoSelectionEvent();
	}

	public void redisplayed() {
		buildDbTree();
		tree.setFocus();
	}
	
}
