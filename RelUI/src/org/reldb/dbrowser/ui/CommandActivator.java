package org.reldb.dbrowser.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.handlers.MenuItem;

/*
 * A combination of a ToolItem, for display on a ToolBar, and an optional reference to a menu handler.
 * 
 * When the ToolItem is active, the associated menu item will be active and can launch the ToolItem.
 * 
 * When the ToolItem is inactive or disposed, the associated menu item will be inactive.
 * 
 * If mnuClass is null, there is no associated menu item.
 * 
 */
public class CommandActivator extends ToolItem {

	private Class<? extends MenuItem>mnuClass;
	
	public void activate() {
		if (mnuClass == null)
			return;
		try {
			Method activate = mnuClass.getMethod("activate", new Class<?>[] {Class.class, CommandActivator.class});
			activate.invoke(null, mnuClass, this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void deactivate() {
		if (mnuClass == null)
			return;
		try {
			Method deactivate = mnuClass.getMethod("deactivate", new Class<?>[] {Class.class});
			deactivate.invoke(null, mnuClass);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public CommandActivator(Class<? extends MenuItem> mnuClass, ToolBar parent, int style) {
		super(parent, style);
		this.mnuClass = mnuClass;
		activate();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		activate();
	}

	public void notifyToolbarDisposed() {
		deactivate();
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
