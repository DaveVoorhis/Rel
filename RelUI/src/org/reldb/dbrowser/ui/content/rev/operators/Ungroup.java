package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Attribute;

public class Ungroup extends UngroupOrUnwrap {
	
	public Ungroup(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "UNGROUP", xpos, ypos);
	}

	@Override
	protected Attribute[] getAvailableAttributes() {
		return getAvailableAttributesForType("RELATION");
	}

}
