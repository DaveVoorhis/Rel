package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.dbrowser.ui.content.rev.Visualiser;

public class Unorder extends Monadic {
	
	public Unorder(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "UNORDER", xpos, ypos);
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return source + " UNORDER() ";		
	}
	
	protected Control obtainControlPanel(Visualiser parent) {return null;}
	
	@Override
	protected void buildControlPanel(Composite container) {}

}
