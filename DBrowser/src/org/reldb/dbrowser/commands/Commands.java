package org.reldb.dbrowser.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public class Commands {

	public static enum Do {
		MakeBackup,
		Refresh, 
		Show, 
		Edit, 
		New, 
		Drop, 
		Design, 
		Rename, 
		Export, 
		ShowSystemObjects, 
		DisplayOk, 
		DisplayAutoClear, 
		ShowRelationHeadings, 
		ShowRelationHeadingAttributeTypes, 
		DisplayEnhancedOutput, 
		SaveAsText, 
		SaveAsHTML, 
		ClearOutput, 
		WrapText, 
		CopyInputToOutput, 
		SaveHistory, 
		SaveFile, 
		InsertFile, 
		LoadFile, 
		FindReplace, 
		NextHistory, 
		PreviousHistory, 
		SpecialCharacters, 
		CopyOutputToInput, 
		InsertFileName,
	}

	private static Map<Do, MenuItem> menuDoMapping = new HashMap<>();
	private static Map<Do, HashSet<ManagedToolbar>> commandDoMapping = new HashMap<>();
	
	public static void addCommandActivator(CommandActivator activator) {
		synchronized (commandDoMapping) {
			HashSet<ManagedToolbar> toolbars = commandDoMapping.get(activator.getCommand());
			if (toolbars == null)
				toolbars = new HashSet<>();
			toolbars.add(activator.getManagedToolbar());
			commandDoMapping.put(activator.getCommand(), toolbars);
		}
	}
	
	public static void removeCommandActivator(CommandActivator activator) {
		synchronized (commandDoMapping) {
			HashSet<ManagedToolbar> toolbars = commandDoMapping.get(activator.getCommand());
			if (toolbars == null)
				return;
			toolbars.remove(activator.getManagedToolbar());
		}
	}
	
	public static CommandActivator getCommandActivator(Do command) {
		synchronized (commandDoMapping) {
			HashSet<ManagedToolbar> toolbars = commandDoMapping.get(command);
			if (toolbars == null)
				return null;
			for (ManagedToolbar toolbar: toolbars) {
				if (!toolbar.isDisposed() && toolbar.isVisible())
					for (ToolItem toolItem: toolbar.getItems())
						if (toolItem instanceof CommandActivator && !toolItem.isDisposed())
							if (((CommandActivator) toolItem).getCommand() == command)
								return (CommandActivator)toolItem;
			}
			return null;
		}
	}
	
	public static void linkCommand(Do command, AcceleratedMenuItem menuItem) {
		menuDoMapping.put(command, menuItem);
		menuItem.getParent().addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				CommandActivator activator = getCommandActivator(command);
				if (activator != null && activator.isFullyEnabled()) {
					menuItem.setEnabled(true);
					// remove old listeners
					Listener[] oldListeners = menuItem.getListeners(SWT.Selection);
					for (Listener oldListener: oldListeners)
						menuItem.removeListener(SWT.Selection, oldListener);
					// handle CHECK and RADIO menu items
					if ((menuItem.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0) {
						menuItem.setSelection(activator.getSelection());
						menuItem.addListener(SWT.Selection, e -> activator.setSelection(!activator.getSelection()));
					}
					// add new listener
					menuItem.addListener(SWT.Selection, e -> activator.click());
					// acquire the CommandActivator's tooltip
					menuItem.setToolTipText(activator.getToolTipText());
				} else
					menuItem.setEnabled(false);
			}
		});
		menuItem.setEnabled(false);
	}

	public static MenuItem getMenuItem(Do command, CommandActivator toolitem) {
		return menuDoMapping.get(command);
	}

}
