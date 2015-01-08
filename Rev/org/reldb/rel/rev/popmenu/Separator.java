package org.reldb.rel.rev.popmenu;

import javax.swing.JSeparator;

public class Separator {
	public Separator(PopupMenu parent) {
		JSeparator separator = new JSeparator();
		parent.getJPopupMenu().add(separator);
	}
}