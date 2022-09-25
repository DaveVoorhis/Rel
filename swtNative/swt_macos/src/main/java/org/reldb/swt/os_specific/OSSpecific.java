package org.reldb.swt.os_specific;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

public class OSSpecific {

	public static void launch(String appName, Listener exitListener, Listener aboutListener, Listener preferencesListener) {
		new CocoaUIEnhancer(appName).earlyStartup(exitListener, aboutListener, preferencesListener);
	}

	public static void addFileMenuItems(Menu menu) {
	}
	
	public static void addHelpMenuItems(Menu menu) {
	}	
	
}
