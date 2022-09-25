package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.filtersorter.SearchAdvancedPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.utilities.StringUtils;

public class Restrict extends Monadic {
	
	private SearchAdvancedPanel searchAdvancedPanel;
	
	public Restrict(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Restrict", xpos, ypos);
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("true");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}
	
	private void save() {
		String quotedDefinition = StringUtils.quote(operatorLabel.getText());
		getDatabase().updatePreservedStateOperator(getID(), quotedDefinition);
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new FillLayout());
		Heading heading = getHeadingOfParameter(0);
		Iterator<Attribute> attributes = heading.getAttributes();
		Vector<Attribute> attributeList = new Vector<>();
		while (attributes.hasNext())
			attributeList.add(attributes.next());
		searchAdvancedPanel = new SearchAdvancedPanel(attributeList, container);
		searchAdvancedPanel.addListener(SWT.Activate, e -> { 
			container.getShell().pack();
		});
		if (!operatorLabel.getText().equalsIgnoreCase("true")) {
			searchAdvancedPanel.setManualOverride(true);
			searchAdvancedPanel.setManualOverrideText(operatorLabel.getText());
		}
	}

	@Override
	protected void controlPanelOkPressed() {
		searchAdvancedPanel.ok();
		operatorLabel.setText(searchAdvancedPanel.getWhereClause());
		save();
		pack();
	}
	
	@Override
	protected void controlPanelCancelPressed() {
		searchAdvancedPanel.cancel();
		load();
		pack();
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		if (operatorLabel.getText().length() == 0)
			return null;
		return source + " WHERE " + operatorLabel.getText();		
	}

}
