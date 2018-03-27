package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.content.filtersorter.OrderPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Order extends Monadic {

	private OrderPanel orderPanel;

	public Order(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "ORDER", xpos, ypos);
	}

	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("");
		else {
			String defn = tuple.getAttributeValue("Definition").toString();
			if (defn.equals("()"))
					defn = "";
			operatorLabel.setText(defn);
		}
	}

	private void save() {
		getDatabase().updatePreservedStateOperator(getID(), operatorLabel.getText());
	}

	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new FillLayout());
		orderPanel = new OrderPanel(container, SWT.None);
		orderPanel.setText(operatorLabel.getText());
		orderPanel.setAvailableAttributes(getAttributeNamesOfParameter(0));
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(orderPanel.getText());
		save();
		pack();
	}

	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "(" + source + ") ORDER (" + operatorLabel.getText() + ")";
	}

}
