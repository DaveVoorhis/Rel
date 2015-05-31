package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Update extends OperatorWithControlPanel {
	
	private static class Updating { 
		
		private String expression;
		private String as;
		
		Updating(String expression, String as) {
			this.expression = expression;
			this.as = as;
		}
		
		Updating() {
			this.expression = "";
			this.as = "";
		}
		
		String getExpression() {return expression;}
		
		void setExpression(String expression) {this.expression = expression;}
		
		String getAs() {return as;}
		
		void setAs(String as) {this.as = as;}
		
		public String toString() {
			return as + " := " + expression;
		}
	}
	
	private Vector<Updating> updatings;
	
	public Update(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "UPDATE", xpos, ypos);
		addParameter("Operand"); 
		load();
		pack();
	}

	private String getSpecificationAsString() {
		String specification = "";
		for (Updating updating: updatings) {
			if (updating.getAs().trim().length() == 0)
				continue;
			if (specification.length() > 0)
				specification += ", ";
			specification += updating.toString();
		}
		return specification;
	}
	
	private String getSpecificationAsRelation() {
		int id = 0;
		String specification = "RELATION {ID INTEGER, attribute CHAR, expression CHAR} {\n";
		for (Updating updating: updatings) {
			if (updating.getAs().trim().length() == 0)
				continue;
			if (id > 0)
				specification += ",\n";
			specification += "  TUPLE {";
			specification += "ID " + (id++) + ", ";
			specification += "attribute '" + updating.getAs() + "', ";
			specification += "expression '" + updating.getExpression() + "'}";
		}
		specification += "}";
		return specification;
	}
	
	protected void load() {
		updatings = new Vector<Updating>();
		Tuples tuples = getDatabase().getPreservedStateUpdate(getID());
		if (tuples == null)
			return;
		Iterator<Tuple> i = tuples.iterator();
		while (i.hasNext()) {
			Tuple t = i.next();
			updatings.add(new Updating(t.getAttributeValue("expression").toString(), t.getAttributeValue("attribute").toString()));
		}
		updatings.add(new Updating());
		operatorLabel.setText(getSpecificationAsString());
	}
	
	private void save() {
		getDatabase().updatePreservedStateUpdate(getID(), getSpecificationAsRelation());
	}
	
	private void addRow(Composite parent, Updating r) {
		Text as = new Text(parent, SWT.NONE);
		as.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		as.setText(r.getAs());		
		as.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setAs(as.getText());
			}
		});
		
		new Label(parent, SWT.NONE);
		
		Text expression = new Text(parent, SWT.NONE);
		expression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression.setText(r.getExpression());
		expression.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setExpression(expression.getText());
			}
		});
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(3, false));
		
		(new Label(container, SWT.None)).setText("Attribute");
		(new Label(container, SWT.None)).setText(":=");
		(new Label(container, SWT.None)).setText("Expression");
		
		for (Updating extending: updatings)
			addRow(container, extending);

		addRowAddButton(container);
	}

	private void addRowAddButton(Composite container) {
		Button addRow = new Button(container, SWT.None);
		addRow.setText("+");
		addRow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				addRow.dispose();
				Updating updating = new Updating();
				updatings.addElement(updating);
				addRow(container, updating);
				addRowAddButton(container);	
				container.getShell().pack();
			}
		});
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(getSpecificationAsString());
		save();
		pack();
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "UPDATE " + source + ": {" + operatorLabel.getText() + "}";
	}
	
    protected void delete() {
		getDatabase().removeOperator_Update(getID());
    	super.delete();
    }

}
