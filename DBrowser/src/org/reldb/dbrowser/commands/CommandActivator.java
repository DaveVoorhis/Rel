package org.reldb.dbrowser.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
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
	private String iconName;
    private boolean visible = false;
	
	public CommandActivator(Do command, ManagedToolbar toolBar, String iconName, int style, String tooltipText, Listener listener) {
		super(toolBar, style);
		this.iconName = iconName;
		setToolTipText(tooltipText);
		setImage(IconLoader.loadIcon(iconName));
		addListener(SWT.Selection, listener);
		if (command != null) {
			menuItem = Commands.getMenuItem(command, this);
			addListener(SWT.Paint, e -> {
				synchronized (iconName) {
					Commands.addCommandDoMapping(command, CommandActivator.this);
					visible = true;
				}
			});
			Timer stateTimer = new Timer();
			stateTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO - Not visible? Don't deactivate unless there's no other visible CommandActivator for the given command
					// TODO - Visible? Always activate.
					CommandActivator.this.getDisplay().asyncExec(() -> {
						if (isDisposed() || getParent().isDisposed()) {
							stateTimer.cancel();
							synchronized (iconName) {
								Commands.removeCommandDoMapping(command);
								visible = false;
							}
						} else if (getParent().isVisible()) {
							synchronized (iconName) {
								Commands.addCommandDoMapping(command, CommandActivator.this);
								visible = true;
							}
						} else if (!getParent().isVisible()) {
							synchronized (iconName) {
								Commands.removeCommandDoMapping(command);
								visible = false;
							}
						}
					});
				}
			}, 1000, 1000);			
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (menuItem != null)
			menuItem.setEnabled(true);
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
		return visible;
	}

	public boolean isFullyEnabled() {
		return !isDisposed() && isVisible() && isEnabled();
	}

	public String getIconName() {
		return iconName;
	}
	
	public void checkSubclass() {}
}
