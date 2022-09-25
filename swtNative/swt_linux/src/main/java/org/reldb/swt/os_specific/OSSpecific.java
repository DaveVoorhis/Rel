package org.reldb.swt.os_specific;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class OSSpecific {

	private static Listener exitListener;
	private static Listener aboutListener;
	private static Listener preferencesListener;
	
	private static String appName;
	
	public static void launch(String appName, Listener exitListener, Listener aboutListener, Listener preferencesListener) {		
		OSSpecific.appName = appName;
		OSSpecific.exitListener = exitListener;
		OSSpecific.aboutListener = aboutListener;
		OSSpecific.preferencesListener = preferencesListener;
	}

	public static void addFileMenuItems(Menu menu) {
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem preferences = new MenuItem(menu, SWT.PUSH);
		preferences.setText("Preferences...");
		preferences.addListener(SWT.Selection, preferencesListener);
		MenuItem exit = new MenuItem(menu, SWT.PUSH);
		exit.setText("Exit " + appName);
		exit.addListener(SWT.Selection, exitListener);
	}
	
	public static void addHelpMenuItems(Menu menu) {
		MenuItem about = new MenuItem(menu, SWT.PUSH);
		about.setText("About " + appName);
		about.addListener(SWT.Selection, aboutListener);
	}
	
}