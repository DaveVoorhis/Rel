package org.reldb.dbrowser.ui.content.rel;

import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
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

		treeRoots = new HashMap<String, TreeItem>();
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		sashForm.setWeights(new int[] {1, 4});
		
		buildDbTree();
	}

	public void NewItem() {
		// TODO Auto-generated method stub
		
	}

	public void DropItem() {
		// TODO Auto-generated method stub
		
	}

	public boolean getShowSystemObjects() {
		return showSystemObjects;
	}

	public void setShowSystemObjects(boolean selection) {
		showSystemObjects = selection;
		buildDbTree();
	}
	
	private void buildSubtree(String section, String query, String displayAttributeName) {
		TreeItem root = treeRoots.get(section);
		if (root == null) {
			root = new TreeItem(tree, SWT.NONE);
			root.setText(section);
			treeRoots.put(section, root);
		}
		if (query != null) {
			Tuples relvarNames = connection.getTuples(query);
			if (relvarNames != null)
				for (Tuple tuple: relvarNames) {
					TreeItem relvar = new TreeItem(root, SWT.NONE);
					relvar.setText(tuple.getAttributeValue(displayAttributeName).toString());
				}
		}
	}
	
	private void buildDbTree() {
		for (TreeItem root: treeRoots.values())
			root.removeAll();

		String sysStr = (showSystemObjects) ? null : "Owner <> 'Rel'";
		String andSysStr = ((sysStr != null) ? (" AND " + sysStr) : "");
		String whereSysStr = ((sysStr != null) ? (" WHERE " + sysStr) : "");
		
		buildSubtree("Variables", "(sys.Catalog WHERE NOT isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name");
		buildSubtree("Views", "(sys.Catalog WHERE isVirtual" + andSysStr + ") {Name} ORDER (ASC Name)", "Name");
		buildSubtree("Operators", "EXTEND (sys.Operators UNGROUP Implementations)" + whereSysStr + ": {opName := Signature || IF ReturnsType <> '' THEN ' RETURNS ' || ReturnsType ELSE '' END IF} {opName} ORDER (ASC opName)", "opName");
		buildSubtree("Types", "(sys.Types" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name");
		buildSubtree("Constraints", "(sys.Constraints" + whereSysStr + ") {Name} ORDER (ASC Name)", "Name");
		buildSubtree("Forms", null, null);
		buildSubtree("Reports", null, null);
		buildSubtree("Scripts", null, null);
	}

	public void redisplayed() {
		buildDbTree();
		tree.setFocus();
	}
}
