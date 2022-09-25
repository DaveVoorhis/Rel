/*******************************************************************************
 * Copyright (c) 2008, 2012 Adobe Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Adobe Systems, Inc. - initial API and implementation
 *     IBM Corporation - cleanup
 *     EclipseSource Inc - modified to run without workbench
 *     Reldb.org - stripped out extraneous code
 *******************************************************************************/
package org.reldb.swt.os_specific;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSApplication;
import org.eclipse.swt.internal.cocoa.NSMenu;
import org.eclipse.swt.internal.cocoa.NSMenuItem;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.OS;

/**
 * Heavy modification of Eclipse's CocoaUIEnhancer to provide the standard "About" and "Preference" menu items.
 * 
 * @noreference this class is not intended to be referenced by any client.
 * @since 1.0
 */
public class CocoaUIEnhancer extends CocoaUtil {

	private static final int kAboutMenuItem = 0;
	private static final int kPreferencesMenuItem = 2;
	private static final int kHideApplicationMenuItem = 6;
	private static final int kQuitMenuItem = 10;

	static long sel_toolbarButtonClicked_;
	static long sel_preferencesMenuItemSelected_;
	static long sel_aboutMenuItemSelected_;
	static long sel_exitMenuItemSelected_;

	//	private static final long NSWindowToolbarButton = 3;

	/* This callback is not freed */
	static Callback proc3Args;
	static final byte[] SWT_OBJECT = { 'S', 'W', 'T', '_', 'O', 'B', 'J', 'E', 'C', 'T', '\0' };

	private void init() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
	IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		// TODO: These should either move out of Display or be accessible to
		// this class.
		byte[] types = { '*', '\0' };
		int size = C.PTR_SIZEOF;
		int align = C.PTR_SIZEOF == 4 ? 2 : 3;

		Class<?> clazz = CocoaUIEnhancer.class;

		proc3Args = new Callback(clazz, "actionProc", 3); //$NON-NLS-1$
		// call getAddress
		Method getAddress = Callback.class.getMethod("getAddress", new Class[0]);
		Object object = getAddress.invoke(proc3Args, (Object[])null);
		long proc3 = convertToLong(object);
		if (proc3 == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);

		// call objc_allocateClassPair
		Field field = OS.class.getField("class_NSObject");
		Object fieldObj = field.get(OS.class);

		Object[] args = makeArgs(fieldObj, "SWTCocoaEnhancerDelegate", wrapPointer(0));
		object = invokeMethod(OS.class, "objc_allocateClassPair", args);

		long cls = convertToLong(object);

		args = makeArgs(wrapPointer(cls), SWT_OBJECT, wrapPointer(size), Byte.valueOf((byte) align), types);
		invokeMethod(OS.class, "class_addIvar", args);

		// Add the action callback
		args = makeArgs(wrapPointer(cls), wrapPointer(sel_toolbarButtonClicked_), wrapPointer(proc3), "@:@");
		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		args = makeArgs(wrapPointer(cls), wrapPointer(sel_preferencesMenuItemSelected_), wrapPointer(proc3), "@:@");
		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		args = makeArgs(wrapPointer(cls), wrapPointer(sel_aboutMenuItemSelected_), wrapPointer(proc3), "@:@");
		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		args = makeArgs(wrapPointer(cls), wrapPointer(sel_exitMenuItemSelected_), wrapPointer(proc3), "@:@");
		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$
		
		invokeMethod(OS.class, "objc_registerClassPair", makeArgs(cls));
	}

	SWTCocoaEnhancerDelegate delegate;
	private long delegateJniRef;

	/**
	 * Class that is able to intercept and handle OS events from the toolbar and menu.
	 * 
	 * @since 3.1
	 */

	private String fAboutActionName;
	private String fQuitActionName;
	private String fHideActionName;
	
	private static Listener exitListener;
	private static Listener aboutListener;
	private static Listener preferencesListener;

	/**
	 * Default constructor
	 */
	public CocoaUIEnhancer(String productName) {

		if (fAboutActionName == null)
			fAboutActionName = "About " + productName; //$NON-NLS-1$
		if (fQuitActionName == null)
			fQuitActionName = "Quit " + productName;
		if (fHideActionName == null)
			fHideActionName = "Hide " + productName;

		try {
			if (sel_toolbarButtonClicked_ == 0) {
				sel_toolbarButtonClicked_ = registerName("toolbarButtonClicked:"); //$NON-NLS-1$
				sel_preferencesMenuItemSelected_ = registerName("preferencesMenuItemSelected:"); //$NON-NLS-1$
				sel_aboutMenuItemSelected_ = registerName("aboutMenuItemSelected:"); //$NON-NLS-1$
				sel_exitMenuItemSelected_ = registerName("exitMenuItemSelected:"); //$NON-NLS-1$
				init();
			}
		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private long registerName(String name) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<OS> clazz = OS.class;
		Object object = invokeMethod(clazz, "sel_registerName", new Object[] { name });
		return convertToLong(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup(Listener exitListener, Listener aboutListener, Listener preferencesListener) {
		CocoaUIEnhancer.exitListener = exitListener;
		CocoaUIEnhancer.aboutListener = aboutListener;
		CocoaUIEnhancer.preferencesListener = preferencesListener;
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				startupInUI();
			}
		});
	}

	void log(Exception e) {
		System.out.println("ERROR: " + e);
		e.printStackTrace();
	}

	private void hookApplicationMenu() {
		try {
			// create About menu command
			NSMenu mainMenu = NSApplication.sharedApplication().mainMenu();
			NSMenuItem mainMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, mainMenu, "itemAtIndex", new Object[] { wrapPointer(0) });
			NSMenu appMenu = mainMenuItem.submenu();

			// add the about action
			NSMenuItem aboutMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu, "itemAtIndex", new Object[] { wrapPointer(kAboutMenuItem) });
			aboutMenuItem.setTitle(NSString.stringWith(fAboutActionName));
			aboutMenuItem.setTarget(delegate);
			invokeMethod(NSMenuItem.class, aboutMenuItem, "setAction", new Object[] { wrapPointer(sel_aboutMenuItemSelected_) });

			// rename the hide action if we have an override string
			if (fHideActionName != null) {
				NSMenuItem hideMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu, "itemAtIndex", new Object[] { wrapPointer(kHideApplicationMenuItem) });
				hideMenuItem.setTitle(NSString.stringWith(fHideActionName));
			}

			// rename the quit action if we have an override string
			if (fQuitActionName != null) {
				NSMenuItem quitMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu, "itemAtIndex", new Object[] { wrapPointer(kQuitMenuItem) });
				quitMenuItem.setTitle(NSString.stringWith(fQuitActionName));
				quitMenuItem.setTarget(delegate);
				invokeMethod(NSMenuItem.class, quitMenuItem, "setAction", new Object[] { wrapPointer(sel_exitMenuItemSelected_) });
			}

			// enable pref menu
			NSMenuItem prefMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu, "itemAtIndex", new Object[] { wrapPointer(kPreferencesMenuItem) });
			prefMenuItem.setEnabled(true);
			prefMenuItem.setTarget(delegate);
			invokeMethod(NSMenuItem.class, prefMenuItem, "setAction", new Object[] { wrapPointer(sel_preferencesMenuItemSelected_) });

		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void createDelegate() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		delegate = new SWTCocoaEnhancerDelegate();
		delegate.alloc().init();
		// call OS.NewGlobalRef
		Method method = OS.class.getMethod("NewGlobalRef", new Class[] { Object.class });
		Object object = method.invoke(OS.class, new Object[] { CocoaUIEnhancer.this });
		delegateJniRef = convertToLong(object);
	}

	private Runnable createDisposer() {
		return new Runnable() {
			public void run() {
				if (delegateJniRef != 0) {
					try {
						invokeMethod(OS.class, "DeleteGlobalRef", new Object[] { CocoaUtil.wrapPointer(delegateJniRef) });
					} catch (Exception e) {
						// theoretically, one of
						// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
						// not expected to happen at all.
						log(e);
					}
				}
				delegateJniRef = 0;

				if (delegate != null)
					delegate.release();
				delegate = null;

			}
		};
	}

	private void startupInUI() {
		try {
			createDelegate();

			if (delegateJniRef == 0)
				SWT.error(SWT.ERROR_NO_HANDLES);

			setDelegate();

			hookApplicationMenu();

			// schedule disposal of callback object
			Runnable disposer = createDisposer();
			Display.getDefault().disposeExec(disposer);

		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void setDelegate() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Field idField = SWTCocoaEnhancerDelegate.class.getField("id");
		Object idValue = idField.get(delegate);

		Object[] args = makeArgs(idValue, SWT_OBJECT, wrapPointer(delegateJniRef));
		invokeMethod(OS.class, "object_setInstanceVariable", args);
	}

	static int actionProc(int id, int sel, int arg0) throws Exception {
		return (int) actionProc((long) id, (long) sel, (long) arg0);
	}

	static long actionProc(long id, long sel, long arg0) throws Exception {
		long[] jniRef = OS_object_getInstanceVariable(id, SWT_OBJECT);
		if (jniRef[0] == 0)
			return 0;

		invokeMethod(OS.class, "JNIGetObject", new Object[] { wrapPointer(jniRef[0]) });

		if (sel == sel_toolbarButtonClicked_) {
			new_NSControl(arg0);
		} else if (sel == sel_preferencesMenuItemSelected_) {
			showPreferences();
		} else if (sel == sel_aboutMenuItemSelected_) {
			showAbout();
		} else if (sel == sel_exitMenuItemSelected_) {
			doExit();
		}

		return 0;
	}

	private static void showAbout() {
		aboutListener.handleEvent(null);
	}

	private static void showPreferences() {
		preferencesListener.handleEvent(null);
	}
	
	private static void doExit() {
		exitListener.handleEvent(null);
	}
	
}
