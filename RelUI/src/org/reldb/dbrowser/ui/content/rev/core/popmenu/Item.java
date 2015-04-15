package org.reldb.dbrowser.ui.content.rev.core.popmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public abstract class Item {
	public Item(PopupMenu parent, String title) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		parent.getJPopupMenu().add(menuItem);
	}
	public abstract void run();
}