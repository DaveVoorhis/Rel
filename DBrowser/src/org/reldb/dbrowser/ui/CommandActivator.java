package org.reldb.dbrowser.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/*
 * A combination of a ToolItem, for display on a ToolBar, and an optional reference to a MenuItem.
 * 
 * When the ToolItem is active, the associated MenuItem will be enabled and can launch the ToolItem.
 * 
 * When the ToolItem is inactive or disposed, the associated MenuItem will be inactive.
 * 
 * If MenuItem is null, there is no associated menu item.
 * 
 */
public class CommandActivator extends ToolItem {

	private MenuItem menuItem;
	
	public CommandActivator(MenuItem menuItem, ToolBar parent, int style) {
		super(parent, style);
		this.menuItem = menuItem;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (menuItem != null)
			menuItem.setEnabled(true);
	}

	public void notifyToolbarDisposed() {
		if (menuItem != null)
			menuItem.setEnabled(false);
	}

	public void click() {
		// cheat to invoke sendSelectionEvent(), which is private
        Class<?>toolItemClass = getClass().getSuperclass().getSuperclass().getSuperclass();	// i.e., Widget class
        Method method;
		try {
			method = toolItemClass.getDeclaredMethod("sendSelectionEvent", int.class, Event.class, boolean.class);
	        method.setAccessible(true);
	        method.invoke(this, SWT.Selection, new Event(), false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void checkSubclass() {}
}
