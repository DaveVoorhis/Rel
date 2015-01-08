package org.reldb.rel.client.string;

import java.io.IOException;
import java.net.MalformedURLException;

import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.shared.Defaults;

public class ClientFromURL {
	
    /** Open a connection. */
    public static StringReceiverClient openConnection(String databaseURL, boolean createDbAllowed) throws NumberFormatException, IOException, MalformedURLException, ClassNotFoundException {
    	if (databaseURL.toLowerCase().startsWith("local:")) {
    		Class.forName("org.reldb.rel.v0.version.Version");
    		if (databaseURL.length() > 6)
    			return new ClientLocal(databaseURL.substring(6).trim(), createDbAllowed);
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
