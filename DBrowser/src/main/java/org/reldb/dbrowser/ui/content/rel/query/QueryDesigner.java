package org.reldb.dbrowser.ui.content.rel.query;

import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class QueryDesigner extends QueryView {
	public QueryDesigner(RelPanel relPanel) {
		super(relPanel, Rev.EDITABLE);
	}
}
