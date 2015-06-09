package org.reldb.dbrowser.ui.content.rel;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelPanel extends Composite {
	
	private DbConnection connection;
	private boolean showSystemObjects = false;

	private Tree tree;
	private HashMap<String, TreeItem> treeRoots;
	
	public static class DbTreeItem {
		
		private boolean canPlay;
		private boolean canNew;
		private boolean canDrop;
		private boolean canDesign;
		private String section;
		private String name;

		DbTreeItem(String section, boolean canPlay, boolean canNew, boolean canDrop, boolean canDesign, String name) {
			this.section = section;
			this.canPlay = canPlay;
			this.canNew = canNew;
			this.canDrop = canDrop;
			this.canDesign = canDesign;
			this.name = name;
		}
		
		DbTreeItem(String section, boolean canPlay, boolean canNew, boolean canDrop, boolean canDesign) {
			this(section, canPlay, canNew, canDrop, canDesign, null);
		}
		
		DbTreeItem() {
			this(null, false, false, false, false, null);
		}
		
		public boolean canPlay() {
			return canPlay;
		}

		public boolean canNew() {
			return canNew;
		}

		public boolean canDrop() {
			return canDrop;
		}

		public boolean canDesign() {
			return canDesign;
		}
		
		public String getSection() {
			return section;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			if (section != null && name != null)
				return section + ": " + name;
			else if (section != null)
				return section;
			else
				return "<none>";
		}
	};
	
	public static interface DbTreeListener {
		public void select(DbTreeItem item);
	}
	
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
		
		treeRoots = new HashMap<String, TreeItem>();
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		sashForm.setWeights(new int[] {1, 4});
		
		buildDbTree();
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
		System.out.println("RelPanel: play " + getSelection().toString());
	}

	public void newItem() {
		System.out.println("RelPanel: new " + getSelection().toString());
	}

	public void dropItem() {
		System.out.println("RelPanel: drop " + getSelection().toString());
	}

	public void designItem() {
		System.out.println("RelPanel: design " + getSelection().toString());
	}

	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}
	
	private void buildSubtree(String section, String query, String displayAttributeName, Predicate<String> filter) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new TreeItem(tree, SWT.NONE);
			root.setText(section);
			treeRoots.put(section, root);
			root.setData(new DbTreeItem(section, false, true, false, false));
		}
		if (query != null) {
			Tuples names = connection.getTuples(query);
			if (names != null)
				for (Tuple tuple: names) {
					String name = tuple.getAttributeValue(displayAttributeName).toString();
					if (filter.test(name)) {
						TreeItem item = new TreeItem(root, SWT.NONE);
						item.setText(name);
						item.setData(new DbTreeItem(section, true, true, true, true, name));
					}
				}
		}
	}

	private void buildSubtree(String section, String query, String displayAttributeName) {
		buildSubtree(section, query, displayAttributeName, (String attributeName) -> true);
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();

		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		Predicate<String> revSysNamesFilter = (String attributeName) -> attributeName.startsWith("sys.rev") ? showSystemObjects : true; 
		buildSubtree("Variables", "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter);
		buildSubtree("Views", "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name", revSysNamesFilter);
		buildSubtree("Operators", "EXTEND (sys.Operators UNGROUP Implementations)" + whereSysStr + ": {opName := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF} {opName} ORDER (ASC opName)", "opName");
		buildSubtree("Types", "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name");
		buildSubtree("Constraints", "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name");
		if (connection.hasRevExtensions() >= 0) {
			buildSubtree("Queries", "UNION {sys.rev.Query {model}, sys.rev.Relvar {model}}", "model");
			buildSubtree("Forms", null, null);
			buildSubtree("Reports", null, null);
			buildSubtree("Scripts", null, null);
		}
	}

	public void redisplayed() {
		buildDbTree();
		tree.setFocus();
	}
	
}
