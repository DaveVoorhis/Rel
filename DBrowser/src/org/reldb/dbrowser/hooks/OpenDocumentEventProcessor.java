package org.reldb.dbrowser.hooks;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.reldb.dbrowser.DBrowser;

public class OpenDocumentEventProcessor implements Listener {
	private ArrayList<String> filesToOpen = new ArrayList<String>(1);
	private boolean retrieved = false;
	
	public synchronized void handleEvent(Event event) {
		if (event.text != null)
			if (retrieved)
				DBrowser.openFile(event.text);
			else
				filesToOpen.add(event.text);
	}
	
	public synchronized String[] retrieveFilesToOpen() {
		try {
			return filesToOpen.toArray(new String[filesToOpen.size()]);
		} finally {
			filesToOpen.clear();
			retrieved = true;
		}
	}

	public synchronized void addFilesToOpen(String[] fileNames) {
		for (String argument: fileNames)
			filesToOpen.add(argument);
	}
}
