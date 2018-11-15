package org.reldb.dbrowser.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.commands.Commands.Do;

/*
 * A combination of a ToolItem for display on a ToolBar, and an optional reference to a MenuItem.
 * 
 * When the ToolItem is active, the associated MenuItem will be enabled and can launch the ToolItem.
 * 
 * When the ToolItem is inactive or disposed, the associated MenuItem will be inactive.
 * 
 */
public class CommandActivator extends ToolItem {
	
	private MenuItem menuItem;
	
	public CommandActivator(Do command, ToolBar toolBar, int style) {
		super(toolBar, style);
		menuItem = Commands.getMenuItem(toolBar, command);
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
	
	/*
	public boolean canExecute(DecoratedMenuItem item) {
		CommandActivator activator = activated.get(getClass());
		if (activator == null)
			return false;
		if ((activator.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0)
			item.setSelection(activator.getSelection());
		return activator.getEnabled();
	}
	
	protected boolean doExecute() {
		CommandActivator activator = activated.get(getClass());
		if (activator != null) {
			if ((activator.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0)
				activator.setSelection(!activator.getSelection());
			activator.click();
			return true;
		} else
			return false;
	}
	*/
	
	public void checkSubclass() {}
}
