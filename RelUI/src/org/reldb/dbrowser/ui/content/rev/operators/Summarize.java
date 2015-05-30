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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rev.Argument;
import org.reldb.dbrowser.ui.content.rev.AttributeListPanel;
import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Summarize extends OperatorWithControlPanel {
	
	private static class Aggregate { 
		private String as;
		private String aggOpName;
		private String expression1;
		private String expression2;
		
		Aggregate(String as, String aggOpName, String expression1, String expression2) {
			this.as = as;
			this.aggOpName = aggOpName;
			this.expression1 = expression1;
			this.expression2 = expression2;
		}
		
		public String getAs() {
			return as;
		}
		
		public void setAs(String as) {
			this.as = as;
		}
		
		public String toString() {
			String aggOpParms = "";
			if (expression1 != null && expression1.trim().length() > 0)
				aggOpParms += expression1;
			if (expression2 != null && expression2.trim().length() > 0) {
				if (aggOpParms.length() > 0)
					aggOpParms += ", ";
				aggOpParms += expression2;
			}
			return as + " := " + aggOpName + "(" + aggOpParms + ")";
		}
		
		public String getAggOpName() {
			return aggOpName;
		}
		
		public void setAggOpName(String aggOpName) {
			this.aggOpName = aggOpName;
		}
		
		public String getExpression1() {
			if (expression1 == null)
				return "";
			return expression1;
		}
		
		public void setExpression1(String expression1) {
			this.expression1 = expression1;
		}
		
		public String getExpression2() {
			if (expression2 == null)
				return "";
			return expression2;
		}
		
		public void setExpression2(String expression2) {
			this.expression2 = expression2;
		}
		
		public String toTuple(int id) {
			return 
					"TUPLE {" +
					"     ID " + id + ", " +
					"     asAttribute '" + as + "', " +
					"     aggregateOp '" + aggOpName + "', " +
					"     expression1 '" + ((expression1 != null) ? expression1 : "") + "', " +
					"     expression2 '" + ((expression2 != null) ? expression2 : "") + "'" +
					"}";
		}
	}
	
	private static class AggOp {
		private String name;
		private int parameterCount;
		
		AggOp(String name, int parameterCount) {
			this.name = name;
			this.parameterCount = parameterCount;
		}
		
		String getName() {return name;}
		
		int getParameterCount() {return parameterCount;}
	}
	
	private AggOp[] aggregateOperators =
	{
			new AggOp("COUNT", 0),
			new AggOp("COUNTD", 1),
			new AggOp("SUM", 1),
			new AggOp("SUMD", 1),
			new AggOp("AVG", 1),
			new AggOp("AVGD", 1),
			new AggOp("MAX", 1),
			new AggOp("MIN", 1),
			new AggOp("AND", 1),
			new AggOp("OR", 1),
			new AggOp("XOR", 1),
			new AggOp("EXACTLY", 2),
			new AggOp("EXACTLYD", 2),
			new AggOp("UNION", 1),
			new AggOp("XUNION", 1),
			new AggOp("D_UNION", 1),
			new AggOp("INTERSECT", 1)
	};
	
	private String byList;
	private Vector<Aggregate> aggregations;
	
	private Argument perArgument;
	
	private Composite controlPanel;
	
	public Summarize(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "SUMMARIZE", xpos, ypos);
		addParameter("Operand"); 
		perArgument = addParameter("Per").getArgument();
		load();
		pack();
	}

	private String getSpecificationAsString() {
		String specification = "";
		if (!perArgument.isVisible())
			specification += "BY " + byList + " ";
		String aggExprs = "";
		for (Aggregate extending: aggregations) {
			if (extending.getAs().trim().length() == 0)
				continue;
			if (aggExprs.length() > 0)
				aggExprs += ", ";
			aggExprs += extending.toString();
		}
		specification += ": {" + aggExprs + "}";
		return specification;
	}
	
	private String getSpecificationAsRelation() {
		int id = 0;
		String specification = "RELATION {ID INTEGER, asAttribute CHAR, aggregateOp CHAR, expression1 CHAR, expression2 CHAR} {\n";
		for (Aggregate extending: aggregations) {
			if (extending.getAs().trim().length() == 0)
				continue;
			if (id > 0)
				specification += ",\n";
			specification += extending.toTuple(id++);
		}
		specification += "}";
		return specification;
	}
	
	protected void load() {
		aggregations = new Vector<Aggregate>();
		Tuples tuples = getDatabase().getPreservedStateSummarize(getID());
		if (tuples == null)
			return;
		byList = "";
		boolean perBySet = false;
		Iterator<Tuple> i = tuples.iterator();
		while (i.hasNext()) {
			Tuple t = i.next();
			if (!perBySet) {
				boolean isby = t.getAttributeValue("isby").toBoolean();
				byList = t.getAttributeValue("byList").toString();
				perArgument.setVisible(!isby);
				perBySet = true;
			}
			String as = t.getAttributeValue("asAttribute").toString();
			String aggregateOp = t.getAttributeValue("aggregateOp").toString();
			String expression1 = t.getAttributeValue("expression1").toString();
			if (expression1.length() == 0)
				expression1 = null;
			String expression2 = t.getAttributeValue("expression2").toString();
			if (expression2.length() == 0)
				expression2 = null;
			aggregations.add(new Aggregate(as, aggregateOp, expression1, expression2));
		}
		if (!perBySet) {
			byList = "";
			perArgument.setVisible(false);
		}
		operatorLabel.setText(getSpecificationAsString());
	}
	
	private void save() {
		if (perArgument.isVisible())
			getDatabase().updatePreservedStateSummarize(getID(), getSpecificationAsRelation());
		else
			getDatabase().updatePreservedStateSummarize(getID(), byList, getSpecificationAsRelation());			
	}
	
	private void setRowVisibility(int index, Text expression1, Text expression2) {
		expression1.setVisible(aggregateOperators[index].getParameterCount() > 0);
		if (!expression1.getVisible())
			expression1.setText("");
		expression2.setVisible(aggregateOperators[index].getParameterCount() > 1);
		if (!expression2.getVisible())
			expression2.setText("");		
	}
	
	private void addRow(Composite parent, Aggregate r) {
		Combo aggOps = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		Text expression1 = new Text(parent, SWT.NONE);
		expression1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression1.setText(r.getExpression1());
		expression1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setExpression1(expression1.getText());
			}
		});
		
		Text expression2 = new Text(parent, SWT.NONE);
		expression2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression2.setText(r.getExpression2());
		expression2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setExpression2(expression2.getText());
			}
		});
		
		int index = 0;
		for (AggOp op: aggregateOperators) {
			aggOps.add(op.getName());
			if (op.getName().equals(r.getAggOpName())) {
					aggOps.select(index);
					setRowVisibility(index, expression1, expression2);
			}
			index++;
		}
		aggOps.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				int selected = aggOps.getSelectionIndex();
				r.setAggOpName(aggOps.getText());
				setRowVisibility(selected, expression1, expression2);
			}
		});
		
		Text as = new Text(parent, SWT.NONE);
		as.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		as.setText(r.getAs());		
		as.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setAs(as.getText());
			}
		});
	}
	
	private AttributeListPanel attributeListPanel = null;
	
	protected void buildPer(Composite container) {
		perArgument.setVisible(true);
		if (attributeListPanel != null) {
			attributeListPanel.dispose();
			attributeListPanel = null;
		}
	}
	
	protected void buildBy(Composite container) {
		perArgument.setOperand(null);
		perArgument.setVisible(false);
		if (attributeListPanel == null)
			attributeListPanel = new AttributeListPanel(container, SWT.None);
		attributeListPanel.setText(byList);
		attributeListPanel.setAvailableAttributes(getAttributeNamesOfParameter(0));
	}
	
	protected void buildPerOrByPanel(Composite container) {
		container.setLayout(new RowLayout(SWT.VERTICAL));
		
		Group perOrByGroup = new Group(container, SWT.SHADOW_IN);
		perOrByGroup.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button btnPer = new Button(perOrByGroup, SWT.RADIO);
		btnPer.setText("PER");
		btnPer.setSelection(perArgument.isVisible());
		btnPer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				buildPer(container);
				controlPanel.getShell().pack();
			}
		});

		Button btnBy = new Button(perOrByGroup, SWT.RADIO);
		btnBy.setText("BY");
		btnBy.setSelection(!perArgument.isVisible());
		btnBy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				buildBy(container);
				controlPanel.getShell().pack();
			}
		});
		
		if (perArgument.isVisible())
			buildPer(container);
		else
			buildBy(container);
	}
	
	protected void buildAggregationPanel(Composite container) {
		container.setLayout(new GridLayout(4, false));
		
		Label col0Heading = new Label(container, SWT.None);
		col0Heading.setText("Operator");
		
		Label col1Heading = new Label(container, SWT.None);
		col1Heading.setText("Expression 1");
		
		Label col2Heading = new Label(container, SWT.None);
		col2Heading.setText("Expression 2");
		
		Label col3Heading = new Label(container, SWT.None);
		col3Heading.setText("As");
		
		for (Aggregate extending: aggregations)
			addRow(container, extending);

		addRowAddButton(container);
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(2, false));

		controlPanel = container;
		
		Composite perOrByPanel = new Composite(container, SWT.NONE);
		perOrByPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		buildPerOrByPanel(perOrByPanel);
		
		Composite aggregationPanel = new Composite(container, SWT.BORDER);
		aggregationPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		buildAggregationPanel(aggregationPanel);
		
		pack();
	}

	private void addRowAddButton(Composite container) {
		Button addRow = new Button(container, SWT.None);
		addRow.setText("+");
		addRow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				addRow.dispose();
				Aggregate extending = new Aggregate("", "", "", "");
				aggregations.addElement(extending);
				addRow(container, extending);
				addRowAddButton(container);	
				container.getShell().pack();
			}
		});
	}

	@Override
	protected void controlPanelOkPressed() {
		if (attributeListPanel != null)
			byList = attributeListPanel.getText();
		operatorLabel.setText(getSpecificationAsString());
		attributeListPanel = null;
		save();
		pack();
	}
	
	@Override
	protected void controlPanelCancelPressed() {
		attributeListPanel = null;
		load();
		pack();
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		String per = "";
		if (perArgument.isVisible())
			per = "PER " + getQueryForParameter(1) + " ";
		return "SUMMARIZE " + source + " " + per + operatorLabel.getText();		
	}
	
    protected void delete() {
		getDatabase().removeOperator_Summarize(getID());
    	super.delete();
    }

}
