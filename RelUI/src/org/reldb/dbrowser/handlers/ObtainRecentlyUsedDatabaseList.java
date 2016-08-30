package org.reldb.dbrowser.handlers;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.reldb.dbrowser.DBrowser;

public class ObtainRecentlyUsedDatabaseList {
	
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items) {
		String[] dbURLs = DBrowser.getRecentlyUsedDatabaseList();
		if (dbURLs.length > 0) {
			MMenuElement separator = MMenuFactory.INSTANCE.createMenuSeparator();
			items.add(separator);
			for (String dbURL: dbURLs) {
				MDirectMenuItem menuItem = MMenuFactory.INSTANCE.createDirectMenuItem();
				menuItem.setLabel("Open " + dbURL);
				menuItem.setIconURI("platform:/plugin/RelUI/icons/" + 
						(dbURL.startsWith("db:") ? "OpenDBLocalIcon.png" : "OpenDBRemoteIcon.png"));
				menuItem.setContributionURI("bundleclass://RelUI/org.reldb.dbrowser.handlers.OpenRecentlyUsed");
				items.add(menuItem);
			}
			separator = MMenuFactory.INSTANCE.createMenuSeparator();
			items.add(separator);
			MDirectMenuItem managementItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			managementItem.setLabel("Clear above list of recently-opened databases");
			managementItem.setContributionURI("bundleclass://RelUI/org.reldb.dbrowser.handlers.ClearRecentlyUsed");
			items.add(managementItem);
			managementItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			managementItem.setLabel("Manage list of recently-opened databases...");
			managementItem.setContributionURI("bundleclass://RelUI/org.reldb.dbrowser.handlers.ManageRecentlyUsed");
			items.add(managementItem);
		}
	}

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {
	}
}
