package ca.mb.armchair.rel3.interpreter;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.storage.*;
import ca.mb.armchair.rel3.server.Server;
import ca.mb.armchair.rel3.shared.Defaults;
import ca.mb.armchair.rel3.version.Version;

/** A self-contained instance of the Rel system. */
public class Instance {
	
	// Static cache of open databases ensures each database is open once, 
	// even if there are multiple InstancesS.
	private static HashMap<File, RelDatabase> openDatabases = null;
	
	private RelDatabase database;
	private Server server = null;
	private String localHostName;
	private boolean evaluate = false;
	private boolean debugOnRun = false;
	private boolean debugAST = false;
	
	public void announceActive(PrintStream output) {
		output.println(Version.getCopyright());
		output.println("Rel is running on " + localHostName);
		output.println("Persistence provided by " + database.getNativeDBVersion());
		output.println(Version.getLicense());
		output.println("Ok.");		
	}
	
    /** Get host name. */
    public String getHost() {
        return localHostName;
    }
		
    /** Get the database. */
    public RelDatabase getDatabase() {
    	return database;
    }
    
	private void usage(File databasePath) {
		System.out.println("Usage: Rel [-f<database>] [-D[port] | [-e] [-v0 | -v1]] < <source>");
		System.out.println(" -f<database>    -- database - default is " + databasePath);
		System.out.println(" -D[port]        -- run as server (port optional - default is " + Defaults.getDefaultPort() + ")");
		System.out.println(" -e              -- evaluate expression");
		System.out.println(" -v0             -- run-time debugging");
		System.out.println(" -v1             -- output AST");
	}
	
	private void initDb(File databasePath) {
		Thread serverShutdownHook = new Thread() {
			public void run() {
				if (server != null)
					server.shutdown();
			}
		};
        Runtime.getRuntime().addShutdownHook(serverShutdownHook);
		if (openDatabases == null) {
			openDatabases = new HashMap<File, RelDatabase>();
			Thread dbShutdownHook = new Thread() {
				public void run() {
					for (RelDatabase database: openDatabases.values())
						database.close();
				}
			};
	        Runtime.getRuntime().addShutdownHook(dbShutdownHook);			
		}
		try {
			localHostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException uhe) {
			localHostName = "<unknown>";
		}
		database = openDatabases.get(databasePath);
		if (database == null) {
			database = new RelDatabase(databasePath);
			database.loadConstraints(System.out);
			openDatabases.put(databasePath, database);
		}
	}

	private File obtaindatabasePath(String databasePath) {
		File f = new File(databasePath);
		if (!f.exists())
			throw new ExceptionFatal("RS0307: Directory " + f + " does not exist.");
		if (!f.isDirectory())
			throw new ExceptionFatal("RS0308: " + f.toString() + " is not a directory.");
		return f;
	}
	
	public Instance(String databasePath) {
		initDb(obtaindatabasePath(databasePath));
	}
	
	public Instance(String args[]) {
		File databasePath = new File(System.getProperty("user.home"));
		if (args.length >= 1) {
			for (int i=0; i<args.length; i++) {
				if (args[i].startsWith("-D")) {
					int portnum = Defaults.getDefaultPort();
					if (args[i].length() > 2) {
						String portnumStr = args[i].substring(2);
						try {
							portnum = Integer.parseInt(portnumStr);
						} catch (NumberFormatException nfe) {
							System.out.println("Error: Invalid port number: " + portnumStr);
							return;
						}
					}
					initDb(databasePath);
					server = new Server(this, portnum);
					return;
				}
				else if (args[i].equals("-v0"))
					debugOnRun = true;
				else if (args[i].equals("-v1"))
					debugAST = true;
				else if (args[i].equals("-e"))
					evaluate = true;
				else if (args[i].startsWith("-f")) {
					if (args[i].length() <= 2) {
						usage(databasePath);
						return;
					}
					databasePath = obtaindatabasePath(args[i].substring(2));
				}
				else {
					usage(databasePath);
					return;
				}
			}
		}
		initDb(databasePath);
		Interpreter interpreter = new Interpreter(database, System.out);
		interpreter.setDebugOnRun(debugOnRun);
		interpreter.setDebugAST(debugAST);
		announceActive(System.out);
		try {
			if (evaluate)
				interpreter.evaluate(System.in).toStream(System.out);
			else
				interpreter.interpret(System.in);
		} catch (Throwable e) {
			if (debugOnRun)
				e.printStackTrace();
			else
				System.out.println(e.getMessage());
		}		
	}
	
	public static void main(String args[]) {
		new Instance(args);
	}

}
