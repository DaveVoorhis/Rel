package org.reldb.rel.v1.interpreter;

import java.io.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.Set;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.shared.Defaults;
import org.reldb.rel.v1.server.Server;
import org.reldb.rel.v1.storage.*;
import org.reldb.rel.v1.storage.RelDatabase.DatabaseConversionException;
import org.reldb.rel.v1.version.Version;

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
		output.println("using " + System.getProperty("java.vendor") + "'s Java version " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor.url"));
		output.println("on " + System.getProperty("os.name") + " version " + System.getProperty("os.version") + " for " + System.getProperty("os.arch") + ".");
		output.println("Persistence is provided by " + database.getNativeDBVersion());
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
	
	private void initDb(File databasePath, boolean createDbAllowed, PrintStream output) {
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
			database = new RelDatabase();
			try {
				database.open(databasePath, createDbAllowed, output);
			} catch (DatabaseConversionException exc) {
				output.println("Database conversion from format v" + exc.getOldVersion() + 
						" to v" + Version.getDatabaseVersion() + " launched...");
				try {
					int oldVersion = exc.getOldVersion();
					// Load detected version's .jar file (should already be done externally if run as Eclipse RCP app.)
					ClassPathHack.addFile(Version.getCoreJarFilename(oldVersion));
					// Instantiate old version as oldRel
					Class<?> oldRelEngine = Class.forName("org.reldb.rel.v" + oldVersion + ".engine.Rel");
					Method oldRelEngineBackup = oldRelEngine.getMethod("backup", String.class, String.class);
					// Backup oldRel's database
					output.println("Running backup...");
					Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
			        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
			        String backupFileName = "relbackup.rel";
			        Path fPath = Files.createFile(Paths.get(databasePath.getAbsolutePath(), backupFileName), attr);
					oldRelEngineBackup.invoke(null, databasePath, fPath.toString());
					// Create new database
					output.println("Creating new database...");
					Path newDbPath = Files.createTempDirectory(databasePath.toString(), attr);
					database.open(newDbPath.toFile(), true, output);
					// Import oldRel's database script into new database
					output.println("Importing backup from old database...");
					Interpreter interpreter = new Interpreter(database, output);
					interpreter.interpret(new FileInputStream(fPath.toFile()));
					database.close();
					// Move Reldb to Backup_Reldb_v<n>/Reldb where <n> is its detected version
					Files.move(Paths.get(newDbPath.toString(), "Reldb"), Paths.get(databasePath.toString(), "Backup_RelDb_v" + oldVersion), StandardCopyOption.REPLACE_EXISTING);
					// Move new database directory to Reldb
					Files.move(newDbPath, Paths.get(databasePath.toString(), "Reldb"), StandardCopyOption.COPY_ATTRIBUTES);
					// Open new database
					database.open(databasePath, false, output);
					output.println("Database conversion complete.");
				} catch (Throwable e1) {
					System.out.println("Unable to complete database conversion due to " + e1);
					e1.printStackTrace();
				}
			}
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
	
	public Instance(String databasePath, boolean createDbAllowed, PrintStream output) {
		initDb(obtaindatabasePath(databasePath), createDbAllowed, output);
	}
    
	private void usage(File databasePath) {
		System.out.println("Usage: RelDBMS [-f<database>] [-D[port] | [-e] [-v0 | -v1]] < <source>");
		System.out.println(" -f<database>    -- database - default is " + databasePath);
		System.out.println(" -D[port]        -- run as server (port optional - default is " + Defaults.getDefaultPort() + ")");
		System.out.println(" -e              -- evaluate expression");
		System.out.println(" -v0             -- run-time debugging");
		System.out.println(" -v1             -- output AST");
	}
	
	private Instance(String args[]) {
		File databasePath = new File("./");
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
					initDb(databasePath, true, System.out);
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
		initDb(databasePath, true, System.out);
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

	public void close() {
		database.close();
	}
	
	public static void main(String args[]) {
		new Instance(args);
	}

}
