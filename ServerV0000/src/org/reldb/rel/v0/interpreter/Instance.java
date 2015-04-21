package org.reldb.rel.v0.interpreter;

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
import org.reldb.rel.v0.server.Server;
import org.reldb.rel.v0.version.Version;
import org.reldb.rel.v0.interpreter.ClassPathHack;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.interpreter.Instance;

/** A self-contained instance of the Rel system. */
public class Instance {
	
	// Static cache of open databases ensures each database is open once, 
	// even if there are multiple InstancesS.
	private static HashMap<File, RelDatabase> openDatabases = null;

	private static String localHostName;
	private static Server server = null;
	
	private RelDatabase database;
	private boolean evaluate = false;
	private boolean debugOnRun = false;
	private boolean debugAST = false;
	
	public void announceActive(PrintStream output) {
		output.println(Version.getCopyright());
		output.println("Rel is running on " + localHostName);
		output.println("using " + System.getProperty("java.vendor") + "'s Java version " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor.url"));
		output.println("on " + System.getProperty("os.name") + " version " + System.getProperty("os.version") + " for " + System.getProperty("os.arch"));
		output.println("with database format v" + Version.getDatabaseVersion() + ".");
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
    
    private static boolean deleteRecursive(File path) throws FileNotFoundException {
        if (!path.exists()) 
        	return true;
        boolean ret = true;
        if (path.isDirectory()) {
            for (File f: path.listFiles()) {
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    private boolean shutdownHookSetup = false;
    
    private void setupShutdownHook() {
    	if (shutdownHookSetup)
    		return;
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
						if (database.isOpen())
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
    	shutdownHookSetup = true;
    }
    
	private void initDb(String databasePath, boolean createDbAllowed, PrintStream output, String[] additionalJarsForClasspath) throws DatabaseFormatVersionException {
		setupShutdownHook();
		File dbPath = new File(databasePath);
		database = openDatabases.get(dbPath);
		if (database == null) {
			database = new RelDatabase();
			database.setAdditionalJarsForJavaCompilerClasspath(additionalJarsForClasspath);
			database.open(dbPath, createDbAllowed, output);
			openDatabases.put(dbPath, database);
		}
	}

	public static void convertToLatestFormat(String dbPath, PrintStream conversionOutput, String[] additionalJarsForClasspath) throws DatabaseFormatVersionException {
		RelDatabase database = new RelDatabase();
		int oldVersion = -1;
		File databasePath = new File(dbPath);
		try {
			database.open(databasePath, false, conversionOutput);
			database.close();
		} catch (DatabaseFormatVersionException dce) {
			oldVersion = dce.getOldVersion();			
		}
		if (oldVersion == -1)
			throw new DatabaseFormatVersionException("RS0415: Database is already the latest format.");
		try {
			String launchMsg = "Database conversion from format v" + oldVersion + " to v" + Version.getDatabaseVersion() + " launched...";
			conversionOutput.println(launchMsg);
			// Load detected version's .jar file (should already be done externally if run as Eclipse RCP app.)
			ClassPathHack.addFile(Version.getCoreJarFilename(oldVersion));
			// Instantiate old version as oldRel
			Class<?> oldRelEngine = Class.forName("org.reldb.rel.v" + oldVersion + ".engine.Rel");
			Method oldRelEngineBackup = oldRelEngine.getMethod("backup", new Class[] {String.class, String.class});
			// Backup oldRel's database
			conversionOutput.println("Running backup...");
	        Path backupScriptPath = Paths.get(databasePath.getAbsolutePath(), "relbackup_v" + oldVersion + ".rel");
			oldRelEngineBackup.invoke(null, databasePath.toString(), backupScriptPath.toString());
			// Create new database
			conversionOutput.println("Creating new database...");
			Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx------");
	        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
			Path newDbDirectory = Files.createDirectory(Paths.get(databasePath.toString(), "_new_Reldb" + Double.toString(Math.random())),  attr);
			database.open(newDbDirectory.toFile(), true, conversionOutput);
			database.setAdditionalJarsForJavaCompilerClasspath(additionalJarsForClasspath);
			// Import oldRel's database script into new database
			conversionOutput.println("Importing backup from old database...");
			Interpreter interpreter = new Interpreter(database, conversionOutput);
			interpreter.interpret(new FileInputStream(backupScriptPath.toFile()));
			database.close();
			conversionOutput.println("Import done");
			Path oldReldbPath = Paths.get(databasePath.toString(), "Reldb");
			Path newReldbPath = Paths.get(newDbDirectory.toString(), "Reldb");
			Path backupReldbPath = Paths.get(databasePath.toString(), "Backup_Reldb_v" + oldVersion);
			// Move Reldb to Backup_Reldb_v<n>/Reldb where <n> is its detected version
			conversionOutput.println("Move " + oldReldbPath + " to " + backupReldbPath);
			deleteRecursive(backupReldbPath.toFile());
			Files.move(oldReldbPath, backupReldbPath, StandardCopyOption.REPLACE_EXISTING);
			// Move new database directory to Reldb
			conversionOutput.println("Move " + newReldbPath + " to " + oldReldbPath);
			deleteRecursive(oldReldbPath.toFile());
			Files.move(newReldbPath, oldReldbPath, StandardCopyOption.REPLACE_EXISTING);
			deleteRecursive(newDbDirectory.toFile());
			conversionOutput.println("Database conversion complete.");
			conversionOutput.close();
			// Re-open, if possible
			database = new RelDatabase();
			database.open(databasePath, false, conversionOutput);
			database.close();
		} catch (Throwable e1) {
			e1.printStackTrace();
			String msg = "Unable to complete database conversion due to " + e1;
			conversionOutput.close();
			throw new ExceptionSemantic(msg);
		}
	}
	
	public Instance(String databasePath, boolean createDbAllowed, PrintStream output, String[] additionalJarsForJavaClasspath) throws DatabaseFormatVersionException {
		initDb(databasePath, createDbAllowed, output, additionalJarsForJavaClasspath);
	}
    
	public Instance(String databasePath, boolean createDbAllowed, PrintStream output) throws DatabaseFormatVersionException {
		initDb(databasePath, createDbAllowed, output, null);
	}
	
	private void usage(String databasePath) {
		System.out.println("Usage: RelDBMS [-f<database>] [-D[port] | [-e] [-v0 | -v1]] < <source>");
		System.out.println(" -f<database>    -- database - default is " + databasePath);
		System.out.println(" -D[port]        -- run as server (port optional - default is " + Defaults.getDefaultPort() + ")");
		System.out.println(" -e              -- evaluate expression");
		System.out.println(" -v0             -- run-time debugging");
		System.out.println(" -v1             -- output AST");
	}
	
	private Instance(String args[]) {
		String databasePath = "./DefaultRelDb";
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
					try {
						initDb(databasePath, true, System.out, null);
					} catch (DatabaseFormatVersionException e) {
						try {
							convertToLatestFormat(databasePath, System.out, null);
							initDb(databasePath, true, System.out, null);
						} catch (DatabaseFormatVersionException e1) {
							System.out.println("Error: unable to convert database to latest version.");
							return;
						}
					}
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
					databasePath = args[i].substring(2);
				}
				else {
					usage(databasePath);
					return;
				}
			}
		}
		try {
			initDb(databasePath, true, System.out, null);
		} catch (DatabaseFormatVersionException e) {
			try {
				convertToLatestFormat(databasePath, System.out, null);
				initDb(databasePath, true, System.out, null);
			} catch (DatabaseFormatVersionException e1) {
				System.out.println("Error: unable to convert database to latest version.");
				return;
			}
		}
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

	public void dbclose() {
		if (database != null)
			database.close();
	}
	
	public static void main(String args[]) {
		new Instance(args);
	}

}
