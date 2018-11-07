package org.reldb.dbrowser;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.reldb.dbrowser.loading.Loading;

public class E4LifeCycle {
	
	@PostContextCreate
	void postContextCreate(final IEventBroker eventBroker, IApplicationContext context) {
		// configure log4j
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		// register for startup completed event
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				// close dynamic splash screen
				Loading.close();
				eventBroker.unsubscribe(this);
			}
		});
		// close static splash screen
		context.applicationRunning();
		// open dynamic splash screen
		Loading.open();
	}

	@PreSave
	void preSave(IEclipseContext workbenchContext) {
	}

	@ProcessAdditions
	void processAdditions(IEclipseContext workbenchContext) {
	}

	@ProcessRemovals
	void processRemovals(IEclipseContext workbenchContext) {
	}
}
