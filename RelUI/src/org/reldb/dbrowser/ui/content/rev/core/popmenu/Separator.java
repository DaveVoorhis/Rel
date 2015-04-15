package org.reldb.dbrowser.ui.content.rev.core.popmenu;

import javax.swing.JSeparator;

public class Separator {
	public Separator(PopupMenu parent) {
		JSeparator separator = new JSeparator();
		parent.getJPopupMenu().add(separator);
	}
}