package org.reldb.dbrowser.handlers;

import java.util.HashMap;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.swt.SWT;
import org.reldb.dbrowser.ui.CommandActivator;

public abstract class MenuItem {
	private static HashMap<Class<? extends MenuItem>, CommandActivator> activated = new HashMap<>();
	
	public static void activate(Class<? extends MenuItem> tag, CommandActivator activator) {
		activated.put(tag, activator);
	}
	
	public static void deactivate(Class<? extends MenuItem> tag) {
		activated.remove(tag);
	}

	public static void clear() {
		activated.clear();
	}
	
	@CanExecute
	public boolean canExecute(MHandledMenuItem item) {
		CommandActivator activator = activated.get(getClass());
		if (activator == null)
			return false;
		if ((activator.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0)
			item.setSelected(activator.getSelection());
		return activator.getEnabled();
	}
	
	@Execute
	public void execute() {
		CommandActivator activator = activated.get(getClass());
		if (activator != null) {
			if ((activator.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0)
				activator.setSelection(!activator.getSelection());
			activator.click();
		}
	}
}
