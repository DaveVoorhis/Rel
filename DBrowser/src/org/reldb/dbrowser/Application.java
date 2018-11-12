package org.reldb.dbrowser;

import java.awt.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.AboutDialog;
import org.reldb.dbrowser.ui.feedback.BugReportDialog;
import org.reldb.dbrowser.ui.feedback.SuggestionboxDialog;
import org.reldb.dbrowser.ui.log.LogWin;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.dbrowser.ui.updates.UpdatesCheckDialog;
import org.reldb.dbrowser.ui.version.Version;
import org.reldb.swt.os_specific.OSSpecific;

public class Application {

	static boolean createdScreenBar = false;
	
	static Shell shell = null;
	
	static boolean isMac() {
		return SWT.getPlatform().equals("cocoa");
	}
	
	private static void quit() {
		Display display = Display.getCurrent();
		Shell[] shells = display.getShells();
		for (Shell shell: shells)
			if (!shell.isDisposed())
				shell.close();
		if (!display.isDisposed()) 
			display.dispose();
		SWTResourceManager.dispose();
		System.exit(0);
	}
	
	private static void createFileMenu(Menu bar) {	
		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);			
		fileItem.setText("File");
		
		Menu menu = new Menu(fileItem);
		fileItem.setMenu(menu);
		
		new DecoratedMenuItem(menu, "&New Database\tCtrl-N", SWT.MOD1 | 'N', "NewDBIcon", e -> DBrowser.newDatabase());
		new DecoratedMenuItem(menu, "Open &local database\tCtrl-l", SWT.MOD1 | 'l', "OpenDBLocalIcon", e -> DBrowser.openLocalDatabase());
		new DecoratedMenuItem(menu, "Open remote database\tCtrl-r", SWT.MOD1 | 'r', "OpenDBRemoteIcon", e -> DBrowser.openRemoteDatabase());
		
		String[] dbURLs = DBrowser.getRecentlyUsedDatabaseList();
		if (dbURLs.length > 0) {
			new MenuItem(menu, SWT.SEPARATOR);
			for (String dbURL: dbURLs)
				new DecoratedMenuItem(menu, "Open " + dbURL, 0, "OpenDBLocalIcon", e -> DBrowser.openDatabase(dbURL));
			new MenuItem(menu, SWT.SEPARATOR);			
			new DecoratedMenuItem(menu, "Clear above list of recently-opened databases", 0, (String)null, e -> DBrowser.clearRecentlyUsedDatabaseList());
			new DecoratedMenuItem(menu, "Manage list of recently-opened databases...", 0, (String)null, e -> DBrowser.manageRecentlyUsedDatabaseList());
		}
		OSSpecific.addFileMenuItems(menu);
	}
	
	private static boolean isWebSite(Control control) {
		return control.getClass().getName().equals("org.eclipse.swt.browser.WebSite");		
	}
		
	private static Method getEditMethod(String methodName, Control control) {
		if (control == null)
			return null;
		if (isWebSite(control)) {
			// Browser browser = (Browser)getParent().getParent();
			Object webSite = control;
			Method getParent;
			try {
				getParent = webSite.getClass().getMethod("getParent");
				Object webSiteParent = getParent.invoke(webSite);
				Method getParentOfParent = webSiteParent.getClass().getMethod("getParent");
				Object browser = getParentOfParent.invoke(webSiteParent);
				return browser.getClass().getMethod(methodName);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		} else {		
			Class<?> controlClass = control.getClass();
			try {
				return (Method)controlClass.getMethod(methodName, new Class<?>[0]);
			} catch (NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
	}
	
	private static boolean isThereSomethingToPaste() {
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
			String textData = (String)clipboard.getContents(textTransfer);
			String htmlData = (String)clipboard.getContents(htmlTransfer);
			return (textData != null && textData.length() > 0) || (htmlData != null && htmlData.length() > 0);
		} finally {
			clipboard.dispose();	
		}
	}

	private static void createEditMenuItem(String methodName, DecoratedMenuItem menuItem) {
		class EditMenuAdapter extends MenuAdapter { 
			Control focusControl;
			LinkedList<Method> menuItemMethods = new LinkedList<Method>();
		};
		EditMenuAdapter menuAdapter = new EditMenuAdapter() {
			@Override
			public void menuShown(MenuEvent arg0) {
				focusControl = menuItem.getDisplay().getFocusControl();
				if (focusControl == null)
					return;
				if (!menuItem.canExecute()) {
					menuItem.setEnabled(false);
					return;
				}
				Method method = getEditMethod(methodName, focusControl);
				if (method != null) {
					menuItemMethods.add(method);
					System.out.println("Application: " + focusControl.getClass() + " supports " + methodName);
					menuItem.setEnabled(true);
				} else {
					if (methodName.equals("clear")) {
						Method selectAll = getEditMethod("selectAll", focusControl);
						Method cut = getEditMethod("cut", focusControl);
						if (selectAll != null && cut != null) {
							menuItemMethods.add(selectAll);
							menuItemMethods.add(cut);
							menuItem.setEnabled(true);
							System.out.println("Application: " + focusControl.getClass() + " supports " + methodName);
						} else
							menuItem.setEnabled(false);
					} else
						menuItem.setEnabled(false);
				}
			}
		};
		menuItem.getParent().addMenuListener(menuAdapter);
		menuItem.addListener(SWT.Selection, evt -> {
			try {
				for (Method method: menuAdapter.menuItemMethods)
					method.invoke(menuAdapter.focusControl, new Object[0]);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		});
		menuItem.setEnabled(false);
	}
	
	private static void createEditMenu(Menu bar) {		
		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
		
		Menu menu = new Menu(editItem);
		editItem.setMenu(menu);
		
		createEditMenuItem("undo", new DecoratedMenuItem(menu, "Undo\tCtrl-Z", SWT.MOD1 | 'Z', "undo"));
		
		int redoAccelerator = SWT.MOD1 | (isMac() ? SWT.SHIFT | 'Z' : 'Y');
		createEditMenuItem("redo", new DecoratedMenuItem(menu, "Redo\tCtrl-Y", redoAccelerator, "redo"));
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		createEditMenuItem("cut", new DecoratedMenuItem(menu, "Cut\tCtrl-X", SWT.MOD1 | 'X', "cut"));
		createEditMenuItem("copy", new DecoratedMenuItem(menu, "Copy\tCtrl-C", SWT.MOD1 | 'C', "copy"));
		createEditMenuItem("paste", new DecoratedMenuItem(menu, "Paste\tCtrl-V", SWT.MOD1 | 'V', "paste") {
			public boolean canExecute() {
				return isThereSomethingToPaste();
			}
		});
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		createEditMenuItem("delete", new DecoratedMenuItem(menu, "Delete\tDel", SWT.DEL, "delete"));
		createEditMenuItem("selectAll", new DecoratedMenuItem(menu, "Select All\tCtrl-A", SWT.MOD1 | 'A', "selectAll"));
		createEditMenuItem("findReplace", new DecoratedMenuItem(menu, "Find/Replace", 0, "edit_find_replace"));
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		createEditMenuItem("specialCharacters", new DecoratedMenuItem(menu, "Special characters", 0, "characters"));
		createEditMenuItem("previousHistory", new DecoratedMenuItem(menu, "Previous history", 0, "previousIcon"));
		createEditMenuItem("nextHistory", new DecoratedMenuItem(menu, "Next history", 0, "nextIcon"));
		createEditMenuItem("clear", new DecoratedMenuItem(menu, "Clear", 0, "clearIcon"));
		createEditMenuItem("loadFile", new DecoratedMenuItem(menu, "Load file", 0, "loadIcon"));
		createEditMenuItem("insertFile", new DecoratedMenuItem(menu, "Insert file", 0, "loadInsertIcon"));
		createEditMenuItem("insertFileName", new DecoratedMenuItem(menu, "Insert file name", 0, "pathIcon"));
		createEditMenuItem("saveFile", new DecoratedMenuItem(menu, "Save file", 0, "saveIcon"));
		createEditMenuItem("saveHistory", new DecoratedMenuItem(menu, "Save history", 0, "saveHistoryIcon"));
 
 		new MenuItem(menu, SWT.SEPARATOR);
 
 		new DecoratedMenuItem(menu, "Copy input to output", 0, "copyToOutputIcon", SWT.CHECK, e -> {});
 		new DecoratedMenuItem(menu, "Wrap text", 0, "wrapIcon", SWT.CHECK, e -> {});
	}

	private static void createDatabaseMenu(Menu bar) {
		MenuItem databaseItem = new MenuItem(bar, SWT.CASCADE);
		databaseItem.setText("Database");
		
		Menu menu = new Menu(databaseItem);
		databaseItem.setMenu(menu);
/*
		new DecoratedMenuItem(menu, "Add Grid...\tCtrl-G", SWT.MOD1 | 'G', IconLoader.loadIcon("newgrid"), event -> {
			
		});
		
		new DecoratedMenuItem(menu, "Link...\tCtrl-L", SWT.MOD1 | 'L', IconLoader.loadIcon("link"), event -> {
			
		});
		
		new DecoratedMenuItem(menu, "Import...\tCtrl-I", SWT.MOD1 | 'I', IconLoader.loadIcon("import"), event -> {
			
		});
		*/
	}
	
	private static void createOutputMenu(Menu bar) {
		MenuItem outputItem = new MenuItem(bar, SWT.CASCADE);
		outputItem.setText("Output");
		
	}
	
	private static void createToolsMenu(Menu bar) {
		MenuItem toolsItem = new MenuItem(bar, SWT.CASCADE);
		toolsItem.setText("Tools");
		
		Menu menu = new Menu(toolsItem);
		toolsItem.setMenu(menu);
		
		new DecoratedMenuItem(menu, "View log", 0, e -> LogWin.open());
		new MenuItem(menu, SWT.SEPARATOR);
		new DecoratedMenuItem(menu, "Submit Feedback", 0, "ButterflyIcon", e -> SuggestionboxDialog.launch(shell));
		new DecoratedMenuItem(menu, "Bug Report", 0, "BugIcon", e -> BugReportDialog.launch(shell));
		new DecoratedMenuItem(menu, "Check for Updates", 0, e -> UpdatesCheckDialog.launch(shell));
	}
	
	private static void createHelpMenu(Menu bar) {
		MenuItem helpItem = new MenuItem(bar, SWT.CASCADE);
		helpItem.setText("Help");
		
		Menu menu = new Menu(helpItem);
		helpItem.setMenu(menu);
		
		OSSpecific.addHelpMenuItems(menu);
	}
	
	private static void createMenuBar(Shell shell) {
		Menu bar = Display.getCurrent().getMenuBar();
		boolean hasAppMenuBar = (bar != null);
		
		if (bar == null)
			bar = new Menu(shell, SWT.BAR);

		// Populate the menu bar once if this is a screen menu bar.
		// Otherwise, we need to make a new menu bar for each shell.
		if (!createdScreenBar || !hasAppMenuBar) {
			
			createFileMenu(bar);
			createEditMenu(bar);
			createOutputMenu(bar);
			createDatabaseMenu(bar);
			createToolsMenu(bar);
			createHelpMenu(bar);
			
			if (!hasAppMenuBar) 
				shell.setMenuBar(bar);
			createdScreenBar = true;
		}
	}
	
	private static Shell createShell() {
		final Shell shell = new Shell(SWT.SHELL_TRIM);
		createMenuBar(shell);
		return shell;
	}

	private static void closeSplash() {
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash != null)
			splash.close();
	}
	
	// If there is a splash screen, return true and execute splashInteraction.
	// If there is no splash screen, return false. Do not execute splashInteraction.
	//
	// Based on https://stackoverflow.com/questions/21022788/is-there-a-chance-to-get-splashimage-work-for-swt-applications-that-require
	private static boolean executeSplashInteractor(Runnable splashInteraction) {
		if (SplashScreen.getSplashScreen() == null)
			return false;
		
		// Non-MacOS
		if (!isMac()) {
			splashInteraction.run();
			closeSplash();
			return true;
		}

		// MacOS
		Display display = Display.getDefault();
		final Semaphore sem = new Semaphore(0);
		Thread splashInteractor = new Thread(() -> {
			splashInteraction.run();
			sem.release();
			display.asyncExec(() -> {});
			closeSplash();
		});
		splashInteractor.start();

		// Interact with splash screen
		while (!display.isDisposed() && !sem.tryAcquire())
			if (!display.readAndDispatch())
				display.sleep();
		
		return true;
	}

	private static Image[] loadIcons(Display display) {
		ClassLoader loader = Application.class.getClassLoader();
		LinkedList<Image> iconImages = new LinkedList<>();
		for (String resourceSpec: Version.getIconsPaths()) {
			InputStream inputStream = loader.getResourceAsStream(resourceSpec);
			if (inputStream != null) {
				iconImages.add(new Image(display, inputStream));
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Unable to load icon " + resourceSpec);
			}
		}
		return iconImages.toArray(new Image[0]);		
	}

	private static void configureLog4j() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	public static void main(String[] args) {
		Display.setAppName(Version.getAppName());
		final Display display = new Display();
		
		configureLog4j();

		OSSpecific.launch(Version.getAppName(),
			event -> quit(),
			event -> new AboutDialog(shell).open(),
			event -> (new Preferences(shell)).show()
		);
		
//		Loading.open();	

		executeSplashInteractor(() -> {
			try {
				/*
				SplashScreen splash = SplashScreen.getSplashScreen();
				if (splash != null) {
					Graphics2D gc = splash.createGraphics();
					Rectangle rect = splash.getBounds();
					System.out.println("Application: rect = " + rect);
					int barWidth = rect.width - 20;
					int barHeight = 10;
					Rectangle progressBarRect = new Rectangle(10, rect.height - 20, barWidth, barHeight);
					gc.draw3DRect(progressBarRect.x, progressBarRect.y, progressBarRect.width, progressBarRect.height, false);
					gc.setColor(Color.green);
					(new Thread() {
						public void run() {
							while (SplashScreen.getSplashScreen() != null) {
								int percent = Loading.getPercentageOfExpectedMessages();
								System.out.println("Application: percent = " + percent);
								int drawExtent = Math.min(barWidth * percent / 100, barWidth);
								gc.fillRect(progressBarRect.x, progressBarRect.y, drawExtent, barHeight);
								splash.update();
								try {
									Thread.sleep(250);
								} catch (InterruptedException e) {
								}
							}							
						}
					}).start();
				}
				*/
				Thread.sleep(300);
			} catch (InterruptedException e1) {
			}
		});
		
		shell = createShell();
		shell.setImages(loadIcons(display));
		shell.setText(Version.getAppID());
		shell.addListener(SWT.Close, e -> {
			shell.dispose();
		});
		shell.addDisposeListener(e -> quit());
		shell.open();		
		shell.layout();
		
		// Loading.open();
		
		DBrowser.launch(args, shell);
	
		// Loading.close();
		
		shell.layout(true);
		
		while (!display.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			} catch (Throwable t) {
				System.out.println("Application: Exception: " + t);
				t.printStackTrace();
			}
		}

		SWTResourceManager.dispose();
	}

	public static MenuItem getBackupMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getRefreshMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getShowMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getEditMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getNewMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDropMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDesignMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getRenameMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getExportMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getShowSystemObjectsMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getCopyInputToOutputMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getClearOutputMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSaveAsHTMLMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSaveAsTextMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDisplayEnhancedOutputMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDisplayOkMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDisplayAutoClearOutputMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getShowRelationHeadingsMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSHowRelationHeadingAttributeTypesMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSpecialCharactersMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getPreviousHistoryMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getNextHistoryMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getClearMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getUndoMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getRedoMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getCutMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getCopyMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getPasteMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSelectAllMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getDeleteMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getFindReplaceMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getLoadFileMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getFileNameMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getInsertFileMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSaveFileMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getSaveHistoryMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MenuItem getWrapTextMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}
}