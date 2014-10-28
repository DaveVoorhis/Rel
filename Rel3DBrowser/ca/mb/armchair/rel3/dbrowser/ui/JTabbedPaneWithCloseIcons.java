package ca.mb.armchair.rel3.dbrowser.ui;

/**
 * A JTabbedPane which has a close icon on each tab.
 *
 * @author  http://forum.java.sun.com/thread.jsp?forum=57&thread=337070
 *
 * With modifications to support permanent tabs and selection listeners, by dave.
 *
 */

import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 *
 * To add a tab, use the method addTab(String, Component)
 *
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
public class JTabbedPaneWithCloseIcons extends JTabbedPane implements MouseListener {
	private static final long serialVersionUID = 0;
	
	private Vector<TabSelectedListener> selectedListeners = new Vector<TabSelectedListener>();
	
	public JTabbedPaneWithCloseIcons() {
		super();
		addMouseListener(this);
	}

	public void addTab(String title, Component component, Icon extraIcon) {
		super.addTab(title, new CloseTabIcon(extraIcon), component);
		fireTabSelectedListener(getTabComponentAt(getTabCount() - 1), getTitleAt(getTabCount() - 1));
	}

	public void addPermanentTab(String title, Component component, Icon icon) {
		super.addTab(title, icon, component);
		fireTabSelectedListener(getTabComponentAt(getTabCount() - 1), getTitleAt(getTabCount() - 1));
	}

	public void addTab(String title, Component component) {
		this.addTab(title, component, null);
	}

	public void addPermanentTab(String title, Component component) {
		this.addPermanentTab(title, component, null);
	}

	public void fireTabSelectedListener(Component tabComponent, String tabTitle) {
		for (TabSelectedListener listener: selectedListeners)
			listener.tabSelected(tabComponent, tabTitle);
	}
	
	public void addTabSelectedListener(TabSelectedListener listener) {
		selectedListeners.add(listener);
	}
	
	public void removeTabSelectedListener(TabSelectedListener listener) {
		selectedListeners.remove(listener);
	}
	
	public void mouseClicked(MouseEvent e) {
		int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabNumber < 0)
			return;
		Icon tabIcon = getIconAt(tabNumber);
		fireTabSelectedListener(getTabComponentAt(tabNumber), getTitleAt(tabNumber));
		if (tabIcon != null && tabIcon instanceof CloseTabIcon) {
			Rectangle rect = ((CloseTabIcon) tabIcon).getBounds();
			if (rect.contains(e.getX(), e.getY()))
				this.removeTabAt(tabNumber);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
