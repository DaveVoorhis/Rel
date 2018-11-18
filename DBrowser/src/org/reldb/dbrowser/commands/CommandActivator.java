package org.reldb.dbrowser.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.commands.Commands.Do;
import org.reldb.dbrowser.ui.IconLoader;

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
	private boolean toolbarDisposed = false;
	private String iconName;
	
	public CommandActivator(Do command, ManagedToolbar toolBar, String iconName, int style, String tooltipText, Listener listener) {
		super(toolBar, style);
		this.iconName = iconName;
		setToolTipText(tooltipText);
		setImage(IconLoader.loadIcon(iconName));
		addListener(SWT.Selection, listener);
		if (command != null)
			menuItem = Commands.getMenuItem(command, this);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (menuItem != null)
			menuItem.setEnabled(true);
	}

	public void notifyToolbarDisposed() {
		System.out.println("CommandActivator: notifyToolbarDisposed in toolItem " + getToolTipText());
		if (menuItem != null)
			menuItem.setEnabled(false);
		toolbarDisposed = true;
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
	
	public boolean isVisible() {
		System.out.println("\t\tCommandActivator: isVisible: 1 in toolItem " + getToolTipText() + " where toolbarDisposed = " + toolbarDisposed);
		ToolBar parent = getParent();
		System.out.println("\t\tCommandActivator: isVisible: 2");
		if (toolbarDisposed || parent.isDisposed()) {
			System.out.println("\t\tCommandActivator: isVisible: 3");
			return false;
		}
		System.out.println("\t\tCommandActivator: isVisible: 4");
		boolean v = parent.isVisible();
		System.out.println("\t\tCommandActivator: isVisible: 5");		
		return v;
	}

	public boolean isFullyEnabled() {
		System.out.println("\tCommandActivator: 1");
		boolean b = isDisposed();
		System.out.println("\tCommandActivator: 2");
		if (b)
			return false;
		System.out.println("\tCommandActivator: 3");
		boolean c = isVisible();
		System.out.println("\tCommandActivator: 4");
		boolean d = isEnabled();
		return !b && c && d;
	}

	public String getIconName() {
		return iconName;
	}
	
	public void checkSubclass() {}
}
