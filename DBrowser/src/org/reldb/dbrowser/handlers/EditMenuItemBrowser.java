package org.reldb.dbrowser.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.reldb.dbrowser.DecoratedMenuItem;
import org.reldb.dbrowser.handlers.EditMenuItem;

public class EditMenuItemBrowser extends EditMenuItem {
	public EditMenuItemBrowser(String methodName) {
		super(methodName);
	}	
	
	boolean isWebSite() {
		return getControl().getClass().getName().equals("org.eclipse.swt.browser.WebSite");		
	}
	
	public boolean canExecute(DecoratedMenuItem item) {
		if (super.canExecute(item))
			return true;
		return isWebSite();
	}

	public void execute() {
		if (doExecute())
			return;
		if (doDefaultMethod())
			return;
		if (isWebSite()) {
			// Browser browser = (Browser)getParent().getParent();
			Object webSite = getControl();
			Method getParent;
			try {
				getParent = webSite.getClass().getMethod("getParent");
				Object webSiteParent = getParent.invoke(webSite);
				Method getParentOfParent = webSiteParent.getClass().getMethod("getParent");
				Object browser = getParentOfParent.invoke(webSiteParent);
				Method editMethod = browser.getClass().getMethod(getEditMethodName());
				editMethod.invoke(browser);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
}
