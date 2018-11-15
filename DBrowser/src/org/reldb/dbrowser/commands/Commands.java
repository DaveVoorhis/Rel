package org.reldb.dbrowser.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;

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
		Delete, 
		SelectAll, 
		Paste, 
		Copy, 
		Cut, 
		Redo, 
		Undo, 
		Clear, 
		NextHistory, 
		PreviousHistory, 
		SpecialCharacters, 
		CopyOutputToInput, 
		InsertFileName,
	}

	private static Map<ToolBar, Map <Do, MenuItem>> commandTable = new HashMap<>();
	private static Map<Do, MenuItem> menuDoMapping = new HashMap<>();

	public static void linkCommand(Do command, DecoratedMenuItem menuItem) {
		menuDoMapping.put(command, menuItem);
	}

	public static MenuItem getMenuItem(ToolBar toolBar, Do command) {
		Map<Do, MenuItem> commandMap = commandTable.get(toolBar);
		if (commandMap != null)
			return commandMap.get(command);
		return null;
	}

	public static void clearToolbar(ToolBar toolBar) {
		// TODO Auto-generated method stub
		
	}

	public static void clearAllToolbars() {
		// TODO Auto-generated method stub
		
	};

}
