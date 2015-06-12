package org.reldb.dbrowser.ui.content.rel;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelPanel extends Composite {
	
	private DbConnection connection;
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
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		tree = new Tree(sashForm, SWT.NONE);
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				DbTreeItem selection = getSelection();
				if (selection == null)
					fireDbTreeNoSelectionEvent();
				else
					fireDbTreeSelectionEvent(selection);
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
		showItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				playItem();
			}
		});
		MenuItem createItem = new MenuItem(menu, SWT.POP_UP);
		createItem.setText("Create");
		createItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				createItem();
			}
		});
		MenuItem dropItem = new MenuItem(menu, SWT.POP_UP);
		dropItem.setText("Drop");
		dropItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				dropItem();
			}
		});
		MenuItem designItem = new MenuItem(menu, SWT.POP_UP);
		designItem.setText("Design");
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
		sashForm.setWeights(new int[] {1, 4});
		
		buildDbTree();
	}

	public DbConnection getConnection() {
		return connection;
	}
	
	public CTabFolder getTabFolder() {
		return tabFolder;
	}
	
	private DbTreeItem getSelection() {
		TreeItem items[] = tree.getSelection();
		if (items == null || items.length == 0)
			return null;
		TreeItem item = items[0];
		return (DbTreeItem)item.getData();
	}
	
	private Vector<DbTreeListener> listeners = new Vector<DbTreeListener>();
	
	public void addDbTreeListener(DbTreeListener listener) {
		listeners.add(listener);
	}
	
	public void removeDbTreeListener(DbTreeListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireDbTreeSelectionEvent(DbTreeItem item) {
		for (DbTreeListener listener: listeners)
			listener.select(item);
	}
	
	protected void fireDbTreeNoSelectionEvent() {
		fireDbTreeSelectionEvent(new DbTreeItem());
	}
	
	public void playItem() {
		getSelection().play();
	}

	public void createItem() {
		getSelection().create();
	}

	public void dropItem() {
		getSelection().drop();
	}

	public void designItem() {
		getSelection().design();
	}

	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}
	
	private void buildSubtree(String section, String query, String displayAttributeName, Predicate<String> filter, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new TreeItem(tree, SWT.NONE);
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
						item.setText(name);
						item.setData(new DbTreeItem(section, player, creator, dropper, designer, name));
					}
				}
		}
	}

	private void buildSubtree(String section, String query, String displayAttributeName, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer) {
		buildSubtree(section, query, displayAttributeName, (String attributeName) -> true, player, creator, dropper, designer);
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();

		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		Predicate<String> revSysNamesFilter = (String attributeName) -> attributeName.startsWith("sys.rev") ? showSystemObjects : true; 
		
		buildSubtree("Variables", "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter, 
			new VarRealPlayer(this), new VarRealCreator(this), new VarRealDropper(this), new VarRealDesigner(this));
		
		buildSubtree("Views", "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter,
			new VarViewPlayer(this), new VarViewCreator(this), new VarViewDropper(this), new VarViewDesigner(this));
		
		buildSubtree("Operators", "EXTEND (sys.Operators UNGROUP Implementations)" + whereSysStr + ": {opName := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF} {opName} ORDER (ASC opName)", "opName",
			new OperatorPlayer(this), new OperatorCreator(this), new OperatorDropper(this), new OperatorDesigner(this));
		
		buildSubtree("Types", "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			null, new TypeCreator(this), new TypeDropper(this), null);
		
		buildSubtree("Constraints", "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name",
			null, new ConstraintCreator(this), new ConstraintDropper(this), new ConstraintDesigner(this));
		
		if (connection.hasRevExtensions() >= 0) {
			buildSubtree("Queries", "UNION {sys.rev.Query {model}, sys.rev.Relvar {model}}", "model",
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
