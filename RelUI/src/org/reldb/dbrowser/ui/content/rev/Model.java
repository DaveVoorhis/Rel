/*
 * Model.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package org.reldb.dbrowser.ui.content.rev;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.reldb.dbrowser.ui.RevDatabase;

/**
 * Defines a layered pane in which visualised classes may be manipulated.
 *
 * @author Dave Voorhis
 */
public class Model extends Composite {

	private int lastMouseX;
	private int lastMouseY;

	private Rev rev;

	private String modelName;

	/** Ctor */
	public Model(Rev rev, String modelName, Composite parent) {
		super(parent, SWT.NONE);

		this.rev = rev;
		this.modelName = modelName;

		addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				lastMouseX = e.x;
				lastMouseY = e.y;
				enablePopupMenu();
			}
		});
	}

	private Menu preservedPopupMenu;

	void disablePopupMenu() {
		if (getMenu() == null)
			return;
		preservedPopupMenu = getMenu();
		setMenu(null);
	}

	void enablePopupMenu() {
		if (getMenu() != null)
			return;
		if (preservedPopupMenu == null)
			return;
		setMenu(preservedPopupMenu);
		preservedPopupMenu = null;
	}

	public Point getLastMousePosition() {
		return new Point(lastMouseX, lastMouseY);
	}

	public void removeEverything() {
		Collection<Visualiser> visualisers = new HashSet<Visualiser>();
		for (Control child : getChildren())
			if (child instanceof Visualiser)
				visualisers.add((Visualiser) child);
		for (Visualiser visualiser : visualisers)
			visualiser.delete();
		rev.fireModelChangeEvent();
	}

	void clear() {
		for (Control control : getChildren())
			control.dispose();
	}

	public int getVisualiserCount() {
		int count = 0;
		for (Control child : getChildren())
			if (child instanceof Visualiser)
				count++;
		return count;
	}

	public Visualiser getVisualiser(String visualiserName) {
		for (Control child : getChildren()) {
			if (!(child instanceof Visualiser))
				continue;
			Visualiser childVisualiser = (Visualiser) child;
			if (childVisualiser.getID().compareTo(visualiserName) == 0)
				return childVisualiser;
		}
		return null;
	}

	public Visualiser getPossibleDropTarget(int i, int j, Visualiser visualiser) {
		for (Control child : getChildren()) {
			if (!(child instanceof Visualiser))
				continue;
			Visualiser childVisualiser = (Visualiser) child;
			if (visualiser.getBounds().intersects(childVisualiser.getBounds())
					&& childVisualiser.canReceiveDropOf(visualiser))
				return childVisualiser;
		}
		return null;
	}

	public RevDatabase getDatabase() {
		return rev.getDatabase();
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String name) {
		modelName = name;
	}

	public Rev getRev() {
		return rev;
	}

}
