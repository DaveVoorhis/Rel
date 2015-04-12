package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class RelPanel extends Composite {
	
	private DbConnection connection;
	
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
		
		Tree tree = new Tree(sashForm, SWT.NONE);
		
		TreeItem trtmVariables = new TreeItem(tree, SWT.NONE);
		trtmVariables.setText("Variables");
		Tuples relvarNames = connection.getTuples("(sys.Catalog WHERE NOT isVirtual) {Name} ORDER (ASC Name)");
		if (relvarNames != null)
			for (Tuple tuple: relvarNames) {
				TreeItem relvar = new TreeItem(trtmVariables, SWT.NONE);
				relvar.setText(tuple.getAttributeValue("Name").toString());
			}

		TreeItem trtmViews = new TreeItem(tree, SWT.NONE);
		trtmViews.setText("Views");
		Tuples viewNames = connection.getTuples("(sys.Catalog WHERE isVirtual) {Name} ORDER (ASC Name)");
		if (viewNames != null)
			for (Tuple tuple: viewNames) {
				TreeItem view = new TreeItem(trtmViews, SWT.NONE);
				view.setText(tuple.getAttributeValue("Name").toString());
			}
		
		TreeItem trtmOperators = new TreeItem(tree, SWT.NONE);
		trtmOperators.setText("Operators");
		Tuples opSignatures = connection.getTuples("EXTEND sys.Operators UNGROUP Implementations: {opName := Signature || ' RETURNS ' || ReturnsType} {opName} ORDER (ASC opName)");
		if (opSignatures != null)
			for (Tuple tuple: opSignatures) {
				TreeItem opSignature = new TreeItem(trtmOperators, SWT.NONE);
				opSignature.setText(tuple.getAttributeValue("opName").toString());
			}
		
		TreeItem trtmTypes = new TreeItem(tree, SWT.NONE);
		trtmTypes.setText("Types");
		Tuples typeNames = connection.getTuples("sys.Types {Name} ORDER (ASC Name)");
		if (typeNames != null)
			for (Tuple tuple: typeNames) {
				TreeItem type = new TreeItem(trtmTypes, SWT.NONE);
				type.setText(tuple.getAttributeValue("Name").toString());
			}
		
		TreeItem trtmConstraints = new TreeItem(tree, SWT.NONE);
		trtmConstraints.setText("Constraints");
		Tuples constraintNames = connection.getTuples("sys.Constraints {Name} ORDER (ASC Name)");
		if (constraintNames != null)
			for (Tuple tuple: constraintNames) {
				TreeItem constraint = new TreeItem(trtmConstraints, SWT.NONE);
				constraint.setText(tuple.getAttributeValue("Name").toString());
			}

		TreeItem trtmForms = new TreeItem(tree, SWT.NONE);
		trtmForms.setText("Forms");
		
		TreeItem trtmReports = new TreeItem(tree, SWT.NONE);
		trtmReports.setText("Reports");
		
		TreeItem trtmScripts = new TreeItem(tree, SWT.NONE);
		trtmScripts.setText("Scripts");
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		sashForm.setWeights(new int[] {1, 4});
	}
}
