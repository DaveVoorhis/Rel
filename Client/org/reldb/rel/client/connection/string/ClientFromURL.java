package org.reldb.rel.client.connection.string;

import java.io.IOException;
import java.net.MalformedURLException;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.shared.Defaults;

public class ClientFromURL {
	
    /** Open a connection. */
    public static StringReceiverClient openConnection(String databaseURL, boolean createDbAllowed, CrashHandler crashHandler, String[] additionalJars) throws NumberFormatException, IOException, MalformedURLException, ClassNotFoundException, DatabaseFormatVersionException {
    	if (databaseURL.toLowerCase().startsWith("local:")) {
    		if (databaseURL.length() > 6)
    			return new ClientLocal(databaseURL.substring(6).trim(), createDbAllowed, crashHandler, additionalJars);
    		else
    			throw new MalformedURLException("Please specify a local database as local:<directory>");
    	} else {
    		ClassPathHack.addFile("relshared.jar");
        	String hostName = databaseURL;
        	int port = Defaults.getDefaultPort();
        	int colonPosition = databaseURL.indexOf(':');
        	if (colonPosition >= 0) {
        		hostName = databaseURL.substring(0, colonPosition);
        		String portString = databaseURL.substring(colonPosition + 1);
       			port = Integer.parseInt(portString);
        	}
        	return new ClientNetwork(hostName, port);		
    	}
    }

}
