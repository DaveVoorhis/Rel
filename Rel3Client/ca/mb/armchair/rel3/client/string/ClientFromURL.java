package ca.mb.armchair.rel3.client.string;

import java.io.IOException;
import java.net.MalformedURLException;

import ca.mb.armchair.rel3.client.utilities.ClassPathHack;
import ca.mb.armchair.rel3.shared.Defaults;

public class ClientFromURL {
	
    /** Open a connection. */
    public static StringReceiverClient openConnection(String databaseURL) throws NumberFormatException, IOException, MalformedURLException, ClassNotFoundException {
    	if (databaseURL.toLowerCase().startsWith("local:")) {
    		Class.forName("ca.mb.armchair.rel3.version.Version");
    		if (databaseURL.length() > 6)
    			return new ClientLocal(databaseURL.substring(6).trim());
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
