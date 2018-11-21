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
	private Do command;
    private boolean visible = false;
    private ManagedToolbar toolbar;

    private Timer stateTimer = new Timer();
    
    protected void notifyVisible() {
    	Commands.addCommandActivator(this);
    }
    
    protected void notifyHidden() {
    	Commands.removeCommandActivator(this);
    }
    
    private void visible() {
    	if (visible)
    		return;
    	visible = true;
    	notifyVisible();
    }
    
    private void hidden() {
    	if (!visible)
    		return;
    	visible = false;
    	notifyHidden();
    }

	public CommandActivator(Do command, ManagedToolbar toolBar, String iconName, int style, String tooltipText, Listener listener) {
		super(toolBar, style);
		this.command = command;
		this.toolbar = toolBar;
		this.iconName = iconName;
		setToolTipText(tooltipText);
		if (iconName != null)
			setImage(IconLoader.loadIcon(iconName));
		if (listener != null)
			addListener(SWT.Selection, listener);
		if (command != null) {
			menuItem = Commands.getMenuItem(command, this);
			addListener(SWT.Paint, e -> visible());
			stateTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (isDisposed()) {
						stateTimer.cancel();
						hidden();
					} else
						getDisplay().asyncExec(() -> {
							if (isDisposed() || getParent().isDisposed()) {
								stateTimer.cancel();
								hidden();
							} else if (getParent().isVisible()) {
								visible();
							} else {
								hidden();
							}
						});
				}
			}, 250, 250);
		}
	}

	public void dispose() {
		if (command != null) {
			stateTimer.cancel();
			hidden();
		}
	}
	
	public void setEnabled(boolean enabled) {
		if (isDisposed() || super.isDisposed())
			return;
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
	
	public Do getCommand() {
		return command;
	}

	public ManagedToolbar getManagedToolbar() {
		return toolbar;
	}
	
	public String toString() {
		return "CommandActivator [" + command + ", " + iconName + "]" + (isDisposed() ? " *disposed*" : "");
	}
	
	public void checkSubclass() {}
}
