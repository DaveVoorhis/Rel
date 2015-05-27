package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Attribute;

public class Unwrap extends UngroupOrUnwrap {
	
	public Unwrap(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "UNWRAP", xpos, ypos);
	}

	@Override
	protected Attribute[] getAvailableAttributes() {
		return getAvailableAttributesForType("TUPLE");
	}

}
