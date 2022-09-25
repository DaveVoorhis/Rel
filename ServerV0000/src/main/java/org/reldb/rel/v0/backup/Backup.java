package org.reldb.rel.v0.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;

import org.reldb.rel.exceptions.ExceptionFatal;

import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.version.Version;

public class Backup {
	
	public static boolean backup(RelDatabase database, PrintStream outputStream) {
		String backupScript = "";
		try {
        	ClassLoader loader = database.getClass().getClassLoader();
        	URL backupScriptURL = loader.getResource("org/reldb/rel/v" + Version.getDatabaseVersion() + "/backup/DatabaseToScript.rel");
        	if (backupScriptURL == null) {
        		new ExceptionFatal("RS0414: Unable to find backup script.");
        	}
        	BufferedReader in = new BufferedReader(new InputStreamReader(backupScriptURL.openStream()));
		    String line;
		    while ((line = in.readLine()) != null) {
		    	backupScript += line + System.lineSeparator();
		    }
		    in.close();
		} catch (IOException ioe) {
			new ExceptionFatal("RS0412: Unable to load backup script", ioe);
		}
		try {
			Interpreter.executeStatement(database, backupScript, outputStream);
			return true;
		} catch (Exception e) {
			new ExceptionFatal("RS0413: Unable to run backup script", e);
			return false;
		}
	}

}
