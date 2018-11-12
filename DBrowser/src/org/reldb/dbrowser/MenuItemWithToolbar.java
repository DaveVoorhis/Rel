package org.reldb.dbrowser;

import java.util.HashMap;

import org.eclipse.swt.SWT;

public abstract class MenuItemWithToolbar {
	private static HashMap<Class<? extends MenuItemWithToolbar>, CommandActivator> activated = new HashMap<>();
	
	public static void activate(Class<? extends MenuItemWithToolbar> tag, CommandActivator activator) {
		activated.put(tag, activator);
	}
	
	public static void deactivate(Class<? extends MenuItemWithToolbar> tag) {
		activated.remove(tag);
	}

	public static void clear() {
		activated.clear();
	}
	
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
	
	public void execute() {
		doExecute();
	}
}
