package org.reldb.dbrowser.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
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
	private static Map<Do, CommandActivator> activatorDoMapping = new HashMap<>();
	
	public static void linkCommand(Do command, DecoratedMenuItem menuItem) {
		menuDoMapping.put(command, menuItem);
		menuItem.getParent().addMenuListener(new MenuAdapter() {
			Listener listener = null;
			@Override
			public void menuShown(MenuEvent arg0) {
				CommandActivator activator = activatorDoMapping.get(command);
				if (activator != null && activator.isFullyEnabled()) {
					menuItem.setEnabled(true);
					Listener[] oldListeners = menuItem.getListeners(SWT.Selection);
					for (Listener oldListener: oldListeners)
						menuItem.removeListener(SWT.Selection, oldListener);
					listener = e -> activator.click();
					menuItem.addListener(SWT.Selection, listener);
				} else {
					menuItem.setEnabled(false);
				}
			}
		});
		menuItem.setEnabled(false);
	}

	public static MenuItem getMenuItem(Do command, CommandActivator toolitem) {
		activatorDoMapping.put(command, toolitem);
		return menuDoMapping.get(command);
	}
	
	public static void clearToolbar(ToolBar toolBar) {
		System.out.println("Commands: clearToolbar " + toolBar.getClass().getName());
		for (ToolItem toolItem: toolBar.getItems()) {
			Iterator<Entry<Do, CommandActivator>> items = activatorDoMapping.entrySet().iterator();
			while (items.hasNext()) {
				Entry<Do, CommandActivator> entry = items.next();
				if (entry.getValue() == toolItem) {
					items.remove();
				}
			}
		}
	}

}
