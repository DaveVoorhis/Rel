package ca.mb.armchair.rel3.client.stream;

import java.io.IOException;
import java.net.MalformedURLException;

import ca.mb.armchair.rel3.client.utilities.ClassPathHack;
import ca.mb.armchair.rel3.shared.Defaults;

public class ClientFromURL {

    /** Open a connection. */
    public static StreamReceiverClient openConnection(String databaseURL) throws NumberFormatException, IOException, MalformedURLException {
    	if (databaseURL.toLowerCase().startsWith("local:"))
    		if (databaseURL.length() > 6)
    			return new ClientLocalConnection(databaseURL.substring(6).trim());
    		else
    			throw new MalformedURLException("Please specify a local database as local:<directory>");
    	else {
    		ClassPathHack.addFile("relshared.jar");
			ClassPathHack.addFile("commons-codec-1.4.jar");
			ClassPathHack.addFile("commons-logging-1.1.1.jar");
			ClassPathHack.addFile("httpclient-4.1.3.jar");
			ClassPathHack.addFile("httpclient-cache-4.1.3.jar");
			ClassPathHack.addFile("httpcore-4.1.4.jar");
			ClassPathHack.addFile("httpmime-4.1.3.jar");    		
        	String hostName = databaseURL;
        	int port = Defaults.getDefaultPort();
        	int colonPosition = databaseURL.indexOf(':');
        	if (colonPosition >= 0) {
        		hostName = databaseURL.substring(0, colonPosition);
        		String portString = databaseURL.substring(colonPosition + 1);
       			port = Integer.parseInt(portString);
        	}
        	return new ClientNetworkConnection(hostName, port);
    	}
    }

}
