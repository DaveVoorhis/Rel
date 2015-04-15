package org.reldb.dbrowser.ui.content.rev.core.popmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class PopupMenu extends MouseAdapter {
	private JPopupMenu popup;
	private int popupX;
	private int popupY;

	public PopupMenu(JComponent parent) {
		popup = new JPopupMenu();
		parent.addMouseListener(this);
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupX = e.getX();
			popupY = e.getY();
			popup.show(e.getComponent(), popupX, popupY);
		}
	}

	public int getPopupX() {
		return popupX;
	}

	public int getPopupY() {
		return popupY;
	}

	public JPopupMenu getJPopupMenu() {
		return popup;
	}
}