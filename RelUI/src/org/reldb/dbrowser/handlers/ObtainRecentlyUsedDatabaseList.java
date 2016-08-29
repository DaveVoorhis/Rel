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
		for (String dbURL: dbURLs) {
			MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			dynamicItem.setLabel("Open " + dbURL);
			dynamicItem.setIconURI("platform:/plugin/RelUI/icons/" + 
					(dbURL.startsWith("local:") ? "OpenDBLocalIcon.png" : "OpenDBRemoteIcon.png"));
			dynamicItem.setContributionURI("bundleclass://RelUI/org.reldb.dbrowser.handlers.OpenRecentlyUsed");
			items.add(dynamicItem);
		}
	}

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {
	}
}
