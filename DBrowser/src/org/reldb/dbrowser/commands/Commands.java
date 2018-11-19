package org.reldb.dbrowser.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;

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
	private static Map<Do, CommandActivator> commandDoMapping = new HashMap<>();

	public static void addCommandDoMapping(Do command, CommandActivator activator) {
		System.out.println("Commands: add mapping for " + command + ": " + activator.getToolTipText());
		commandDoMapping.put(command, activator);
	}
	
	public static void removeCommandDoMapping(Do command) {
		System.out.println("Commands: remove mapping for " + command);
		commandDoMapping.remove(command);
	}
	
	public static CommandActivator getCommandDoMapping(Do command) {
		return commandDoMapping.get(command);
	}
	
	public static void linkCommand(Do command, DecoratedMenuItem menuItem) {
		menuDoMapping.put(command, menuItem);
		menuItem.getParent().addMenuListener(new MenuAdapter() {
			Listener listener = null;
			@Override
			public void menuShown(MenuEvent arg0) {
				CommandActivator activator = commandDoMapping.get(command);
				if (activator != null && activator.isFullyEnabled()) {
					menuItem.setEnabled(true);
					// remove old listeners
					Listener[] oldListeners = menuItem.getListeners(SWT.Selection);
					for (Listener oldListener: oldListeners)
						menuItem.removeListener(SWT.Selection, oldListener);
					// add new listener
					listener = e -> activator.click();
					menuItem.addListener(SWT.Selection, listener);
					if ((menuItem.getStyle() & (SWT.CHECK | SWT.RADIO)) != 0)
						menuItem.setSelection(activator.getSelection());
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
