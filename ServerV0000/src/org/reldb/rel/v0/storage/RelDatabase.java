package org.reldb.rel.v0.storage;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.external.DirClassLoader;
import org.reldb.rel.v0.generator.*;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.languages.tutoriald.parser.*;
import org.reldb.rel.v0.storage.catalog.*;
import org.reldb.rel.v0.storage.relvars.RelvarDefinition;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.RelvarMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarPrivate;
import org.reldb.rel.v0.storage.relvars.RelvarPrivateCell;
import org.reldb.rel.v0.storage.relvars.RelvarReal;
import org.reldb.rel.v0.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v0.storage.tables.KeyTableNames;
import org.reldb.rel.v0.storage.tables.KeyTables;
import org.reldb.rel.v0.storage.tables.RegisteredTupleIterator;
import org.reldb.rel.v0.storage.tables.Table;
import org.reldb.rel.v0.storage.tables.TablePrivate;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.builtin.*;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.version.Version;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;
import org.reldb.rel.v0.vm.VirtualMachine;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.*;

public class RelDatabase {

	public final static String systemOwner = "Rel";

    private Environment environment;
    private DatabaseConfig dbConfigurationNormal;
    private DatabaseConfig dbConfigurationAllowCreate;
    private DatabaseConfig dbConfigurationTemporary;
    private DatabaseConfig dbConfigurationTemporaryWithDuplicatesNoComparator;
    private DatabaseConfig dbConfigurationMetadataAllowCreateNoComparator;

    // Metadata
    private ClassCatalog classCatalog;
    private StringBinding stringDataBinding;
	private DatabaseEntry keyTableEntry = new DatabaseEntry();
    private Database relvarDb;
    
    // open real relvar tables
    private Hashtable<String, Database> openTables = new Hashtable<String, Database>();
    
    // temporary private relvar table names
    private HashSet<String> tempTables = new HashSet<String>();
 
    // active transaction per thread
    private Hashtable<Long, RelTransaction> transactions = new Hashtable<Long, RelTransaction>();
    
    // database constraints
    private Hashtable<String, Operator> constraints = new Hashtable<String, Operator>();
    
    // built-in operators
    private HashMap<String, OperatorDefinition> builtinops = new HashMap<String, OperatorDefinition>();

    // Permanent cached operator definitions.
	private HashMap<OperatorSignature, OperatorDefinition> operatorCachePermanent = new HashMap<OperatorSignature, OperatorDefinition>();
    
    // Cached operator definitions
	private HashMap<OperatorSignature, OperatorDefinition> operatorCache = new HashMap<OperatorSignature, OperatorDefinition>();
		
	// Cached type definitions
	private HashMap<String, org.reldb.rel.v0.types.Type> typeCache = new HashMap<String, org.reldb.rel.v0.types.Type>();
	
	// Active registered tuple iterators
	private HashSet<RegisteredTupleIterator> registeredTupleIterators = new HashSet<RegisteredTupleIterator>();
	
    // class loader for external Java-based operators and types
    private DirClassLoader dirClassLoader;
    
    // Relative database directory name
    private static final String databaseHomeRelative = "Reldb";

    // Relative user code directory name
    private static final String userCodeHomeRelative = "RelUserCode";
    
    // Rel user Java code package
    private static final String relUserCodePackage = databaseHomeRelative + "." + userCodeHomeRelative;
    
    // User code home dir
    private String userCodeHome;
    
    // Database home dir
    private String databaseHome = databaseHomeRelative;
    
    // Rel home dir
    private String homeDir;
    
    // Special mode for caching permanent operators and types
    private boolean specialCacheMode = true;
    
    // Custom types folder
    private String customRelvarsDatabase;
    private String customRelvarsHome;
    private ArrayList<String> customRelvars;
        
    /*
     * This class is used to set up custom Comparator used by the Berkeley DB for data (not needed for metadata).
     */
    private static class ComparisonHandler implements Serializable, Comparator<byte[]> {
		private static final long serialVersionUID = 1L;
		private static volatile SerialBinding<ValueTuple> tupleBinding;
		public ComparisonHandler(SerialBinding<ValueTuple> tupleBinding) {
			ComparisonHandler.tupleBinding = tupleBinding;
		}
		@Override
		public int compare(byte[] o1, byte[] o2) {
			ValueTuple v1 = tupleBinding.entryToObject(new DatabaseEntry(o1));
			ValueTuple v2 = tupleBinding.entryToObject(new DatabaseEntry(o2));
			return v1.compareTo(v2);
		}
    }

    private static void applyComparatorTo(Comparator<byte[]> comparator, DatabaseConfig config) {	
    	config.setBtreeComparator(comparator);
    	config.setOverrideBtreeComparator(true);
    	config.setDuplicateComparator(comparator);
    	config.setOverrideDuplicateComparator(true);
    }

    private String getBerkeleyJavaDBVersion() {
    	return JEVersion.CURRENT_VERSION.getVersionString();
    }

    public static void mkdir(String dir) {
		File dirf = new File(dir);
		if (!dirf.exists()) {
			if (!dirf.mkdirs()) {
				String msg = "Unable to create directory: " + dirf;
				throw new ExceptionFatal("RS0324: " + msg);
			}
		}
    }
    
    public void open(File envHome, boolean canCreateDb, PrintStream outputStream) {
    	String usingBerkeleyJavaDBVersion = getBerkeleyJavaDBVersion(); 
    	if (!usingBerkeleyJavaDBVersion.equals(Version.expectedBerkeleyDBVersion))
    		throw new ExceptionFatal("RS0323: Expected to find Berkeley Java DB version " + Version.expectedBerkeleyDBVersion + " but found version " + usingBerkeleyJavaDBVersion + ".\nAn attempted update or re-installation has probably failed.\nPlease make sure je.jar is not read-only, then try the update or re-installation again.");
    	homeDir = envHome.getAbsolutePath();
    	if (homeDir.endsWith("."))
    		homeDir = homeDir.substring(0, homeDir.length() - 1);
    	if (!homeDir.endsWith(java.io.File.separator))
    		homeDir += java.io.File.separator;
 	
    	databaseHome = homeDir + databaseHomeRelative;
    	if (!canCreateDb) {
    		if (!(new File(databaseHome)).exists())
        		throw new ExceptionSemantic("RS0406: Database " + homeDir + " either doesn't exist or isn't a Rel database.");
    	}
    	
    	mkdir(databaseHome);
    	
    	userCodeHome = databaseHome + java.io.File.separator + userCodeHomeRelative;
    
    	dirClassLoader = new DirClassLoader(homeDir);
    	
        try {
	        EnvironmentConfig environmentConfig = new EnvironmentConfig();
	        environmentConfig.setReadOnly(false);
	        environmentConfig.setAllowCreate(true);
	        environmentConfig.setTransactional(true);
	        environmentConfig.setTxnSerializableIsolation(true);
	        
	        dbConfigurationNormal = new DatabaseConfig();
	        dbConfigurationNormal.setReadOnly(false);
	        dbConfigurationNormal.setAllowCreate(false);
	        dbConfigurationNormal.setTransactional(true);
	        dbConfigurationNormal.setSortedDuplicates(false);

	        dbConfigurationAllowCreate = new DatabaseConfig();	        
	        dbConfigurationAllowCreate.setReadOnly(false);
	        dbConfigurationAllowCreate.setAllowCreate(true);
	        dbConfigurationAllowCreate.setTransactional(true);
	        dbConfigurationAllowCreate.setSortedDuplicates(false);
	        
	        dbConfigurationTemporary = new DatabaseConfig();
	        dbConfigurationTemporary.setReadOnly(false);
	        dbConfigurationTemporary.setAllowCreate(true);
	        dbConfigurationTemporary.setTransactional(false);
	        dbConfigurationTemporary.setSortedDuplicates(false);
	        dbConfigurationTemporary.setTemporary(true);
	        
	        dbConfigurationTemporaryWithDuplicatesNoComparator = new DatabaseConfig();
	        dbConfigurationTemporaryWithDuplicatesNoComparator.setReadOnly(false);
	        dbConfigurationTemporaryWithDuplicatesNoComparator.setAllowCreate(true);
	        dbConfigurationTemporaryWithDuplicatesNoComparator.setTransactional(false);
	        dbConfigurationTemporaryWithDuplicatesNoComparator.setSortedDuplicates(true);
	        dbConfigurationTemporaryWithDuplicatesNoComparator.setTemporary(true);
	        
	        dbConfigurationMetadataAllowCreateNoComparator = new DatabaseConfig();
	        dbConfigurationMetadataAllowCreateNoComparator.setReadOnly(false);
	        dbConfigurationMetadataAllowCreateNoComparator.setAllowCreate(true);
	        dbConfigurationMetadataAllowCreateNoComparator.setTransactional(true);
	        dbConfigurationMetadataAllowCreateNoComparator.setSortedDuplicates(false);
	        
	        // Open the class catalog
	        classCatalog = new ClassCatalog(databaseHome, environmentConfig, dbConfigurationMetadataAllowCreateNoComparator);
	        	        
	        // Set up database BTREE and duplicate comparators.
	    	Comparator<byte[]> comparator = new ComparisonHandler(classCatalog.getTupleBinding());	        
	        applyComparatorTo(comparator, dbConfigurationNormal);
	        applyComparatorTo(comparator, dbConfigurationAllowCreate);
	        applyComparatorTo(comparator, dbConfigurationTemporary);
	        
	        // Open the main database environment
	        environment = new Environment(new File(databaseHome), environmentConfig);

	        // Get a code generator
	        Generator generator = new Generator(this, System.out);
	        
	        // Data for key table entries
	        stringDataBinding = new StringBinding();
	        stringDataBinding.objectToEntry("", keyTableEntry);
	        
	        // Open the metadata db.
	        relvarDb = environment.openDatabase(null, "_Relvars", dbConfigurationMetadataAllowCreateNoComparator);	        
	        
	        // Initialise the Catalog
	        (new Catalog(this)).generate(generator);
	        
	        // Construct builtin-in types and operators
	        specialCacheMode = true;
			BuiltinOperators.buildOperators(this);
			BuiltinTypeBuilder.buildTypes(this);
			operatorCachePermanent = operatorCache;
			specialCacheMode = false;

			// Prepare for battle.
			reset();

			// Set up plugin directories
			File homePlugins = new File(homeDir + "Relplugins");
			File databasePlugins = new File(databaseHome + java.io.File.separator + "Relplugins");
			
			if(!homePlugins.exists())
				homePlugins.mkdir();
			if(!databasePlugins.exists())
				databasePlugins.mkdir();
			
			customRelvarsHome = homePlugins.getAbsolutePath() + java.io.File.separator + "relvars";
			customRelvarsDatabase = databasePlugins.getAbsolutePath() + java.io.File.separator + "relvars";
			customRelvars = new ArrayList<String>();
			
			loadPaths(new File(customRelvarsDatabase), new File(customRelvarsHome));
			loadConstraints(outputStream);

        } catch (DatabaseException db) {
        	String msg = "Unable to open database: " + db.getMessage();
        	outputStream.println(msg);
    		db.printStackTrace(System.out);
        	throw new ExceptionFatal("RS0325: " + msg);
        }
    }

	public void registerTupleIterator(RegisteredTupleIterator registeredTupleIterator) {
		registeredTupleIterators.add(registeredTupleIterator);
	}

	public void unregisterTupleIterator(RegisteredTupleIterator registeredTupleIterator) {
		registeredTupleIterators.remove(registeredTupleIterator);
	}
    
    // Close the environment
    public void close() {
        if (environment != null) {
        	System.out.println("Closing database in " + homeDir);
    		System.out.println("\tClosing active tuple iterators in " + homeDir);
    		int activeTupleIterators = 0;
    		try {
    			for (RegisteredTupleIterator tupleIterator: registeredTupleIterators)
    				if (tupleIterator.forceClose())
    					activeTupleIterators++;
    		} catch (Exception e) {
    			System.err.println("\tError closing active tuple iterators: " + homeDir + ": " + e.toString());
    		}
    		if (activeTupleIterators == 1)
    			System.err.println("\t" + activeTupleIterators + " active tuple iterator was closed.");
    		else if (activeTupleIterators > 1)
    			System.err.println("\t" + activeTupleIterators + " active tuple iterators were closed.");
            System.out.println("\tCommitting open transactions in " + homeDir);
            int openTransactions = 0;
        	for (RelTransaction transaction: transactions.values())
        		while (transaction.getReferenceCount() > 0)
        			try {
        				commitTransaction(transaction);
        				openTransactions++;
		            } catch(DatabaseException dbe) {
		                System.err.println("\tError committing active transactions " + homeDir + ": " + dbe.toString());
		            } catch (Exception e) {
		            	System.err.println("\tUnknown shutdown error 1: " + e);
		            }
    		if (openTransactions == 1)
    			System.err.println("\t" + openTransactions + " open transaction was closed.");
    		else if (openTransactions > 1)
    			System.err.println("\t" + openTransactions + " open transactions were closed.");       	
	        System.out.println("\tClosing relvars in " + homeDir);
            try {
            	for (Database table: openTables.values())
            		table.close();
            } catch(DatabaseException dbe) {
                System.err.println("\tError closing internal tables " + homeDir + ": " + dbe.toString());
            } catch (Exception e) {
            	System.err.println("\tUnknown shutdown error 2: " + e);
            }
        	System.out.println("\tPurging temporary data in " + homeDir);
            try {
            	for (String tableName: tempTables)
            		environment.removeDatabase(null, tableName);
            } catch(DatabaseException dbe) {
                System.err.println("\tError removing temporary data storage " + homeDir + ": " + dbe.toString());
            } catch (Exception e) {
            	System.err.println("\tUnknown shutdown error 3: " + e);
            }
        	System.out.println("\tTemporary data purged in " + homeDir);
            try {
            	relvarDb.close();
            } catch(DatabaseException dbe) {
                System.err.println("\tError closing the relvarDb " + homeDir + ": " + dbe.toString());
            } catch (Exception e) {
            	System.err.println("\tUnknown shutdown error 4: " + e);
            }
			System.out.println("\tClosing environment in " + homeDir);
            try {
            	environment.close();
            } catch(DatabaseException dbe) {
                System.err.println("\tError closing the environment " + homeDir + ": " + dbe.toString());
            } catch (Exception e) {
            	System.err.println("\tUnknown shutdown error 5: " + e);
            }
            environment = null;
            try {
            	classCatalog.close();
            } catch(DatabaseException dbe) {
                System.err.println("\tError closing the ClassCatalog in " + homeDir + ": " + dbe.toString());
            } catch (Exception e) {
            	System.err.println("\tUnknown shutdown error 6: " + e);
            }
	        System.out.println("Database " + homeDir + " is closed.");
        }
    }
    
    public void reset() {
    	operatorCache = new HashMap<OperatorSignature, OperatorDefinition>();
    	typeCache.clear();
    }
    
    /** Get the user Java code definition directory. */
    public String getJavaUserSourcePath() {
    	return userCodeHome;
    }
    
    public static String getRelUserCodePackage() {
    	return relUserCodePackage;
    }
    
    private Database openDatabaseRaw(Transaction txn, String tabName, DatabaseConfig configuration) throws DatabaseException {
		Database db = environment.openDatabase(txn, tabName, configuration);
		openTables.put(tabName, db);
		return db;
    }
    
    // This gnarly bit of code ensures a Berkeley Database is open and valid.  If it was opened in a transaction
    // that was rolled back, it will throw an exception when an attempt is made to open a cursor.  In that
    // case, it needs to be re-opened in the current transaction.
    private Database openDatabase(Transaction txn, String tabName, DatabaseConfig configuration) {
		Database db = openTables.get(tabName);
		try {
			if (db == null)
				db = openDatabaseRaw(txn, tabName, configuration);
			else
				db.openCursor(txn, null).close();
		} catch (IllegalStateException de) {
			try {
				db.close();
				db = openDatabaseRaw(txn, tabName, configuration);
			} catch (IllegalStateException de2) {
	    		de2.printStackTrace();
				throw new ExceptionFatal("RS0326: openDatabase: re-open failed: " + de2);
			}
		}
		return db;
    }
    
    private void closeDatabase(String tabName) throws DatabaseException {
    	Database table = openTables.get(tabName);
    	if (table != null) {
    		table.close();
    		openTables.remove(tabName);
    	}
    }
    
	public ValueOperator compileAnonymousOperator(String source, PrintStream outputStream) {
    	Interpreter interpreter = new Interpreter(RelDatabase.this, outputStream);
		try {
			return interpreter.compileAnonymousOperator(source);
		} catch (ParseException pe) {
			throw new ExceptionSemantic("RS0288: Failed compiling anonymous operator " + source + ": " + pe.toString());
		}
	}
    
    private void loadConstraints(final PrintStream outputStream) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    	    	Interpreter interpreter = new Interpreter(RelDatabase.this, outputStream);
	    	    	RelvarSystem constraintsRelvar = (RelvarSystem)openGlobalRelvar(Catalog.relvarConstraints);
	    			TupleIterator ti = constraintsRelvar.iterator(interpreter.getGenerator());
	    			try {
	    				while (ti.hasNext()) {
	    					ValueTuple constraintEntry = ti.next();
	    					String constraintName = constraintEntry.getValues()[0].toString();
	    					String source = constraintEntry.getValues()[1].toString();
	    					try {
	    						Operator constraintCheckOperator = interpreter.getGenerator().beginConstraintDefinition();
	    						interpreter.compileStatement("RETURN " + source + ";");
	    						interpreter.getGenerator().endConstraintDefinition();
	    						constraints.put(constraintName, constraintCheckOperator);
	    					} catch (ParseException pe) {
	    			    		pe.printStackTrace();
	    						throw new ExceptionFatal("RS0327: Failed loading constraint " + constraintName + ": " + pe.toString());
	    					}
	    				}
	    			} finally {
	    				ti.close();
	    			}
	    			return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0328: loadConstraints failed: " + t);    		
    	}
    }
    
    private boolean checkConstraint(VirtualMachine vm, Operator constraintCheck) {
    	vm.execute(constraintCheck);
    	return ((ValueBoolean)vm.pop()).booleanValue();
    }
    
    /** Check the known constraints for validity.  If a constraint fails, return its name.
     If all constraints succeed, return null. */
    public synchronized String checkConstraints(final PrintStream outputStream) {
    	try {
			return (String)((new TransactionRunner() {
				public Object run(Transaction txn) throws Throwable {
			    	VirtualMachine vm = new VirtualMachine(new Generator(RelDatabase.this, System.out), RelDatabase.this, outputStream);
			    	for (Map.Entry<String, Operator> entry: constraints.entrySet()) {
			    		String constraintName = entry.getKey();
			    		try {
				    		if (!checkConstraint(vm, entry.getValue()))
				    			return constraintName;
			    		} catch (ExceptionSemantic es) {
			        		throw new ExceptionSemantic("RS0393: error in CONSTRAINT " + constraintName + ": " + es.getMessage(), es);
			        	} catch (Throwable t) {
			        		t.printStackTrace();
			        		throw new ExceptionFatal("RS0329: checkConstraints failed: " + t);
			        	}
			    	}
			    	return null;
				}
			}).execute(this));
    	} catch (ExceptionSemantic es) {
    		throw es;
		} catch (Throwable e) {
    		e.printStackTrace();
    		throw new ExceptionFatal("RS0392: checkConstraints failed: " + e);
		}
    }
    
    public String getNativeDBVersion() {
    	return "Oracle Berkeley DB Java Edition version " + getBerkeleyJavaDBVersion();
    }
    
    // Store a Relvar name and associated metadata.
    public void putRelvarMetadata(Transaction txn, String relvarName, RelvarMetadata metadata) throws DatabaseException {
    	if (metadata.getCreationSequence() == -1)
    		metadata.setCreationSequence(getUniqueID());
		DatabaseEntry theKey = new DatabaseEntry();
		stringDataBinding.objectToEntry(relvarName, theKey);
		DatabaseEntry theData = new DatabaseEntry();
		classCatalog.getRelvarMetadataBinding().objectToEntry(metadata, theData);
    	relvarDb.put(txn, theKey, theData);
    }
    
    // Retrieve a Relvar's metadata.  Return null if not found.
    public synchronized RelvarMetadata getRelvarMetadata(Transaction txn, String relvarName) throws DatabaseException {
	    DatabaseEntry theKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();
		stringDataBinding.objectToEntry(relvarName, theKey);
		if (relvarDb.get(txn, theKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			return classCatalog.getRelvarMetadataBinding().entryToObject(foundData);
		return null;
    }
    
    // Remove a Relvar's metadata.
    private void dropRelvarMetadata(Transaction txn, String relvarName) throws DatabaseException {
    	DatabaseEntry theKey = new DatabaseEntry();
		stringDataBinding.objectToEntry(relvarName, theKey);
		if (relvarDb.delete(txn, theKey) != OperationStatus.SUCCESS)
			throw new ExceptionFatal("RS0330: unable to drop relvar metadata for " + relvarName);
    }
    
    // Obtain a unique ID
    public synchronized long getUniqueID() {
        File uniquidFile = new File(databaseHome + java.io.File.separatorChar + "unique.id");
        long id = 0, nextid = 1;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(uniquidFile));
            id = dis.readLong();
            nextid = id + 1;
            dis.close();
        } catch (Throwable t) {
            System.out.println("Creating new ID file.");
        }
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(uniquidFile));
            dos.writeLong(nextid);
            dos.close();
            return id;
        } catch (Throwable t) {
    		t.printStackTrace();
            throw new ExceptionFatal("RS0331: " + t.toString());
        }
    }
    
    // Obtain the current thread ID
    private Long getThreadID() {
    	return new Long(Thread.currentThread().getId());
    }

    /** Return true if a relvar exists. */
    private boolean isRelvarExists(Transaction txn, String name) throws DatabaseException {
		return (getRelvarMetadata(txn, name) != null);
    }
    
    public EntryBinding<ValueTuple> getTupleBinding() {
    	return classCatalog.getTupleBinding();
    }
    
    public DatabaseEntry getKeyTableEntry() {
    	return keyTableEntry;
    }
    
    StoredClassCatalog getClassCatalog() {
        return classCatalog.getStoredClassCatalog();
    }
    
    /** Begin transaction. */
    public synchronized RelTransaction beginTransaction() {
    	Long threadID = getThreadID();
    	RelTransaction currentTransaction = transactions.get(threadID);    	
    	if (currentTransaction == null) {
        	try {
        		Transaction txn = environment.beginTransaction(null, null);
        		// TODO - parameterise setLockTimeout value somewhere
        		txn.setLockTimeout(10, TimeUnit.SECONDS);
        		currentTransaction = new RelTransaction(txn);
        		transactions.put(threadID, currentTransaction);
        	} catch (DatabaseException dbe) {
	    		dbe.printStackTrace();
        		throw new ExceptionFatal("RS0332: unable to begin new transaction: " + dbe);
        	}
    	} else
    		currentTransaction.addReference();
    	return currentTransaction;
    }
    
    // Get current transaction in this thread.  Return null if there isn't one.
    private RelTransaction getCurrentTransaction() {
    	Long threadID = getThreadID();
    	return transactions.get(threadID);    	
    }
    
    /** Commit specified transaction */
    private void commitTransactionUnsynchronized(RelTransaction txn) {
   		txn.commit();
   		if (txn.getReferenceCount() == 0)
   			transactions.remove(getThreadID());
    }
    
    /** Commit specified transaction. */
    public synchronized void commitTransaction(RelTransaction txn) {
    	commitTransactionUnsynchronized(txn);
    }
    
    /** Commit current transaction. */
    public synchronized void commitTransaction() {
    	RelTransaction currentTransaction = getCurrentTransaction();
    	if (currentTransaction == null)
    		throw new ExceptionSemantic("RS0208: No transaction is active.");
    	commitTransactionUnsynchronized(currentTransaction);
    }

    /** Roll back specified transaction. */
    private void rollbackTransactionUnsynchronized(RelTransaction txn) {
   		txn.abort();
   		if (txn.getReferenceCount() == 0)
   			transactions.remove(getThreadID());
   		
    }
    
    /** Roll back specified transaction. */
    synchronized void rollbackTransaction(RelTransaction txn) {
    	rollbackTransactionUnsynchronized(txn);
    }

    /** Roll back current transaction if there is one.  Silently return if there isn't one. */
    public synchronized void rollbackTransactionIfThereIsOne() {
    	reset();
    	RelTransaction currentTransaction = getCurrentTransaction();
    	if (currentTransaction == null)
    		return;
    	while (currentTransaction.getReferenceCount() > 0)
    		rollbackTransactionUnsynchronized(currentTransaction);   
    }
    
    /** Roll back current transaction. Throw an exception if there isn't one. */
    public synchronized void rollbackTransaction() {
    	RelTransaction currentTransaction = getCurrentTransaction();
    	if (currentTransaction == null)
    		throw new ExceptionSemantic("RS0209: No transaction is active.");
    	rollbackTransactionUnsynchronized(currentTransaction);    	
    }
    
    private ArrayList<String> getFoldersAt(File f) {
    	ArrayList<String> list = new ArrayList<String>();
		for(File file : f.listFiles())
			if(file.isDirectory())
				list.add(file.getName().trim());
		return list;
    }
    
    private ArrayList<File> getFilesAt(File f) {
    	ArrayList<File> list = new ArrayList<File>();
		for(File file : f.listFiles())
			if(!file.isDirectory())
				list.add(file);
		return list;
    }
    
    public ArrayList<String> getAllCustomTypes() {
		return customRelvars;
    }
    
    private void loadSinglePath(File file) throws MalformedURLException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	URL u = file.toURI().toURL();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[]{u});
    }
    
    private void loadLibFolderContents(String folder, File libFolder) {
    	if(!libFolder.exists() || libFolder.list().length == 0 || !libFolder.isDirectory()) {
			System.out.println("\t" + folder.toUpperCase() + ": " + "lib folder not found or empty.");
			return;
    	}
    	for(File file : getFilesAt(libFolder))
			try {
				if(file.getName().endsWith("jar")) {
					loadSinglePath(file);
					System.out.println("\t" + folder.toUpperCase() + ": " + file.getName() + " was loaded succesfully.");
				} else
					System.out.println("\t" + folder.toUpperCase() + ": " + "Ignored: " + file.getName());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
    }
    
    private void loadPaths(File database, File home) {
    	try {
    		if(!database.exists() && !home.exists()) {
    			database.mkdir();
    			home.mkdir();
    			return;
    		}
    		if(!database.exists()) {
    			database.mkdir();
    		}
    		if(!home.exists()) {
    			home.mkdir();
    			return;
    		}
    		ArrayList<String> homeTypes = getFoldersAt(home);
    		if(homeTypes.isEmpty())
    			System.out.println("No custom relvars found in the home directory");
    		for(String folder : homeTypes) {
    			File typeFolder = new File(home.getAbsolutePath() + java.io.File.separator + folder);
    			if(typeFolder.list().length > 0) {
	    			customRelvars.add(folder);
	    			System.out.println("Custom relvar " + folder + " was succesfully loaded");
	    			File libFolder = new File(typeFolder.getAbsolutePath() + java.io.File.separator + "lib");
	    			loadLibFolderContents(folder, libFolder);
    			} else
    				System.out.println("Custom relvar " + folder + " was not loaded. Folder is empty.");
    		}
    		
    		ArrayList<String> databaseTypes = getFoldersAt(database);
    		if(databaseTypes.isEmpty())
    			System.out.println("No custom relvars found in the database directory");
    		for(String folder : databaseTypes)
    			if(!homeTypes.contains(folder)) {
    				File typeFolder = new File(database.getAbsolutePath() + java.io.File.separator + folder);
    				if(typeFolder.list().length > 0) {
	    				customRelvars.add(folder);
	    				System.out.println("Custom relvar " + folder + "was succesfully loaded");
	    				File libFolder = new File(typeFolder.getAbsolutePath() + java.io.File.separator + "lib");
	    				loadLibFolderContents(folder, libFolder);
    				} else
        				System.out.println("Custom relvar " + folder + " was not loaded. Folder is empty.");
    			} else
    				System.out.println("Duplicate custom relvar " + folder + " found!");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
    
    /** Obtain Catalog as a TupleIterator */
    public synchronized TupleIterator getCatalogTupleIterator(final Generator generator) {
	    return new RegisteredTupleIterator(generator.getDatabase()) {
		    DatabaseEntry foundKey = new DatabaseEntry();
		    DatabaseEntry foundData = new DatabaseEntry();	
		    RelvarMetadata current = null;
		    String currentKey = null;
			public boolean hasNext() {
				if (current != null)
					return true;
				try {
					if (cursor == null) {
						txn = beginTransaction();
						cursor = relvarDb.openCursor(txn.getTransaction(), null);
					}
					if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						current = classCatalog.getRelvarMetadataBinding().entryToObject(foundData);
						currentKey = stringDataBinding.entryToObject(foundKey);
						return true;
					}
				} catch (DatabaseException exp) {
		    		exp.printStackTrace();
					throw new ExceptionFatal("RS0333: Unable to get next Catalog entry: " + exp);					
				}
				return false;
			}
			public ValueTuple next() {
				if (hasNext())
					try {
						// This must parallel the Heading defined by getNewHeading() in RelvarCatalogMetadata
						Value[] values = new Value[] {
							ValueCharacter.select(generator, currentKey),
							ValueCharacter.select(generator, current.getSourceDefinition()),
							ValueCharacter.select(generator, current.getOwner()),
							ValueInteger.select(generator, current.getCreationSequence()),
							ValueBoolean.select(generator, current.isVirtual())
						};
 						return new ValueTuple(generator, values);
					} finally {
						current = null;
					}
				throw new NoSuchElementException();
			}
		};    	
    }
    
    /** Record dependencies */
    private void addDependencies(Generator generator, String object, String dependencyRelvarName, Collection<String> uses) {
    	RelvarSystemDependencies dependencyRelvar = (RelvarSystemDependencies)openGlobalRelvar(dependencyRelvarName);
    	for (String usesname: uses)
    		dependencyRelvar.insertInternal(generator, object, usesname);
    }
    
    /** Remove object dependencies */
    private void removeDependencies(Generator generator, final String object, String dependencyRelvarName) {
    	RelvarSystemDependencies dependencyRelvar = (RelvarSystemDependencies)openGlobalRelvar(dependencyRelvarName);
    	dependencyRelvar.delete(generator, new TupleFilter() {
    		public boolean filter(ValueTuple tuple) {
    			// index 0 is 'Object' attribute
    			return (((ValueCharacter)tuple.getValues()[0]).stringValue().equals(object));
    		}
    	});
    }
     
    /** Append dependency descriptions to output StringBuffer. */
    private void obtainDependencies(Generator generator, final StringBuffer output, final String object, String dependencyRelvarName, final String usedBy) {
    	RelvarSystemDependencies dependencyRelvar = (RelvarSystemDependencies)openGlobalRelvar(dependencyRelvarName);
    	TupleIterator dependencies = dependencyRelvar.getValue(generator).select(new TupleFilter() {
    		public boolean filter(ValueTuple tuple) {
    			// index 1 is 'Uses' attribute
    			return (((ValueCharacter)tuple.getValues()[1]).stringValue().equals(object));
    		}
    	}).iterator();
    	try {
        	int referenceCount = 0;
	    	while (dependencies.hasNext()) {
				if (referenceCount++ < 5) {
					// index 0 is 'Object' attribute
					output.append("\n\tused by " + usedBy + " " + dependencies.next().getValues()[0]);
				} else {
					output.append(" and more...");
					break;
				}
	    	}
    	} finally {
    		dependencies.close();
    	}
    }
    
    /** Return true if a type of the given name has been loaded.  Return null if it can't be found. */
    public synchronized org.reldb.rel.v0.types.Type loadType(Generator generator, String typeName) {
    	if (typeName.equalsIgnoreCase("CHARACTER") || typeName.equalsIgnoreCase("CHAR"))
    		return TypeCharacter.getInstance();
    	else if (typeName.equalsIgnoreCase("INTEGER") || typeName.equalsIgnoreCase("INT"))
    		return TypeInteger.getInstance();
    	else if (typeName.equalsIgnoreCase("BOOLEAN") || typeName.equalsIgnoreCase("BOOL"))
    		return TypeBoolean.getInstance();
    	else if (typeName.equalsIgnoreCase("RATIONAL") || typeName.equalsIgnoreCase("RAT"))
    		return TypeRational.getInstance();
    	org.reldb.rel.v0.types.Type type = typeCache.get(typeName);
    	if (type == null) {
	    	try {
		    	RelvarTypes typesRelvar = (RelvarTypes)openGlobalRelvar(Catalog.relvarTypes);
		    	ValueTuple typeTuple = typesRelvar.getTupleForKey(generator, typeName);
		    	if (typeTuple == null)
		    		return null;
		    	Interpreter interpreter = new Interpreter(RelDatabase.this, System.out);
		    	// Make sure immediate supertypes are loaded first
		    	ValueRelation superTypeNames = (ValueRelation)typeTuple.getValues()[6];
		    	if (superTypeNames.getCardinality() > 0) { 
		    		for (TupleIterator it = superTypeNames.iterator(); it.hasNext();)
		    			loadType(generator, it.next().getValues()[0].stringValue());
		    		// this type should now be cached, because all subtypes of a loaded type will be loaded,
		    		// so if we load the immediate supertype of this type, this type should already be loaded
		    		type = typeCache.get(typeName);
		    		if (type != null)
		    			return type;
		    	}
		    	String language = typeTuple.getValues()[4].toString();
		    	if (language.equals("Rel")) {
					String source = typeTuple.getValues()[1].toString();
					try {
						interpreter.getGenerator().beginTypeRetrieval();
						interpreter.compileStatement(source);
						type = interpreter.getGenerator().endTypeRetrieval();
					} catch (ParseException pe) {
			    		pe.printStackTrace();
						throw new ExceptionFatal("RS0335: Failed loading type " + typeName + ": " + pe.toString());
					}
				} else if (language.equals("Java")) {
					try {
						Class<?>typeClass = (Class<?>) loadClass(typeName);
						if (typeClass == null)
							throw new ExceptionSemantic("RS0210: Unable to load class " + typeName);
			            Constructor<?> ctor = typeClass.getConstructor(new Class[] {org.reldb.rel.v0.generator.Generator.class});
			            if (ctor == null)
			            	throw new ExceptionSemantic("RS0211: Unable to find a constructor of the form " + typeName + "(Generator generator)");
						type = (org.reldb.rel.v0.types.Type)(ctor.newInstance(generator));
					} catch (InstantiationException ie) {
			    		ie.printStackTrace();
						throw new ExceptionFatal("RS0336: Unable to load TYPE " + typeName + " [1]: " + ie.getMessage());
					} catch (IllegalAccessException iae) {
			    		iae.printStackTrace();
						throw new ExceptionFatal("RS0337: Unable to load TYPE " + typeName + " [2]: " + iae.getMessage());				
					}
		    	} else
		    		throw new ExceptionFatal("RS0338: Unrecognised language '" + language + "' in TYPE " + typeName);
				typeCache.put(typeName, type);
		    	// load subtypes
		    	ValueRelation subTypeNames = (ValueRelation)typeTuple.getValues()[5];
		    	for (TupleIterator it = subTypeNames.iterator(); it.hasNext();)
		    		loadType(generator, it.next().getValues()[0].stringValue());
	    	} catch (Throwable t) {
	    		t.printStackTrace();
	    		throw new ExceptionFatal("RS0339: loadType failed: " + t);
	    	}
    	}
    	return type;
    }
    
    /** Return true if type exists */
    public synchronized boolean isTypeExists(Generator generator, String typeName) {
    	if (typeCache.containsKey(typeName))
    		return true;
    	RelvarTypes typesRelvar = (RelvarTypes)openGlobalRelvar(Catalog.relvarTypes);
    	return (typesRelvar.getTupleForKey(generator, typeName) != null);
    }
    
    /** Create type */
    public synchronized void createType(final Generator generator, final String typeName, final String src, final String owner, final String language, final References references, final String superTypeName) {
		if (isTypeExists(generator, typeName))
			throw new ExceptionSemantic("RS0212: TYPE " + typeName + " is already in the database.");
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    	    	RelvarTypes typesRelvar = (RelvarTypes)openGlobalRelvar(Catalog.relvarTypes);
	    	    	Vector<String> superTypeNames = new Vector<String>();
	    	    	if (superTypeName != null)
	    	    		superTypeNames.add(superTypeName);
	    	    	typesRelvar.insertInternal(generator, typeName, src, owner, language, superTypeNames);
	    	    	// Remove any self-reference
	    	    	references.removeReferenceToType(typeName);
	    	    	// Add dependencies
	    	    	addDependencies(generator, typeName, Catalog.relvarDependenciesTypeOperator, references.getReferencedOperators());
	    	    	addDependencies(generator, typeName, Catalog.relvarDependenciesTypeRelvar, references.getReferencedRelvars());
	    	    	addDependencies(generator, typeName, Catalog.relvarDependenciesTypeType, references.getReferencedTypes());
	    	    	// if this is a subtype, update the immediate supertype
	    	    	if (superTypeName != null) {
	    	    		// TODO - maybe force reload of supertype here?
	    	    		typesRelvar.addSubtype(generator, superTypeName, typeName);
	    	    	}
	    			return null;
	    		}
	    	}).execute(this);
			if (!specialCacheMode)
				reset();
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0340: createType failed: " + t);    		
    	}    	
    }
    
    private void removeGeneratedOperators(Generator generator, final String typeName) {
    	ArrayList<String> dropOps = new ArrayList<String>();
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	TupleIterator operatorsIterator = operatorsRelvar.getValue(generator).iterator();
    	try {
	    	while (operatorsIterator.hasNext()) {
	    		ValueTuple operatorTuple = operatorsIterator.next();
    			ValueRelation implementations = (ValueRelation)operatorTuple.getValues()[1];  // attribute 1 of operator tuple is implementations
	    		TupleIterator implementationsIterator = implementations.iterator();
    			try {
		    		while (implementationsIterator.hasNext()) {
		    			ValueTuple implementationTuple = implementationsIterator.next();
		    			if (((ValueCharacter)implementationTuple.getValues()[4]).stringValue().equals(typeName)) {	// attribute 4 of implementation tuple is generation type 
		    				String opSignatureText = ((ValueCharacter)implementationTuple.getValues()[0]).stringValue(); // attr 0 of implementation tuple is signature
		    				dropOps.add(opSignatureText);
		    			}
		    		}
    			} finally {
    				implementationsIterator.close();
    			}
	    	}
    	} finally {
    		operatorsIterator.close();
    	}
    	for (String opSignatureText: dropOps) {
	    	Interpreter interpreter = new Interpreter(RelDatabase.this, System.out);
	    	OperatorSignature signature;
			try {
				signature = interpreter.getOperatorSignature(opSignatureText);
			} catch (ParseException pe) {
	    		pe.printStackTrace();
				throw new ExceptionFatal("RS0341: Unable to retrieve operator signature for '" + opSignatureText + "': " + pe.toString());
			}
    		dropOperatorInternal(generator, signature);    		
    	}
    }
    
    /** Drop type */
    public synchronized void dropType(final Generator generator, final String typeName) {
    	final org.reldb.rel.v0.types.Type type = loadType(generator, typeName);
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			Generator generator = new Generator(RelDatabase.this, System.out);
	    	    	removeGeneratedOperators(generator, typeName);
	    	    	StringBuffer dependencies = new StringBuffer();
	    	    	obtainDependencies(generator, dependencies, typeName, Catalog.relvarDependenciesOperatorType, "OPERATOR");
	    	    	obtainDependencies(generator, dependencies, typeName, Catalog.relvarDependenciesRelvarType, "VAR");
	    	    	obtainDependencies(generator, dependencies, typeName, Catalog.relvarDependenciesTypeType, "TYPE");
	    	    	if (dependencies.length() > 0)
	    	    		throw new ExceptionSemantic("RS0213: Type " + typeName + " may not be dropped due to dependencies:" + dependencies);
	    	    	RelvarTypes typesRelvar = (RelvarTypes)openGlobalRelvar(Catalog.relvarTypes);
	    	    	typesRelvar.deleteInternal(generator, typeName);
	    	    	removeDependencies(generator, typeName, Catalog.relvarDependenciesTypeOperator);
	    	    	removeDependencies(generator, typeName, Catalog.relvarDependenciesTypeRelvar);
	    	    	removeDependencies(generator, typeName, Catalog.relvarDependenciesTypeType);
	    	    	dirClassLoader.unload(getRelUserCodePackage() + "." + typeName);
	    	    	// if this is a subtype, update the immediate supertype
	    	    	if (type instanceof TypeAlpha) {
	    	    		TypeAlpha udt = (TypeAlpha)type;
	    	    		if (udt.isSubtype()) {
		    	    		// TODO - maybe force reload of supertype here?
	    	    			String superTypeName = udt.getSupertype().getSignature();
	    	    			typesRelvar.removeSubtype(generator, superTypeName, typeName);
	    	    		}
	    	    	}
	    	    	typeCache.remove(typeName);
	    			return null;
	    		}
	    	}).execute(this);
	    	reset();	    	
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0342: dropType failed: " + t);    		
    	}
    }
    
    /** Return true if constraint exists */
    public synchronized boolean isConstraintExists(String constraintName) {
    	return constraints.containsKey(constraintName);
    }
    
    /** Create constraint. */
    public synchronized void createConstraint(final Generator generator, final VirtualMachine vm, final String constraintName, final String sourceCode, final Operator operator, final String owner, final References references) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    	    	if (isConstraintExists(constraintName))
	    	    		throw new ExceptionSemantic("RS0214: Constraint " + constraintName + " already exists.");
	    	    	if (!checkConstraint(vm, operator))
	    	    		throw new ExceptionSemantic("RS0215: Constraint " + constraintName + " returns false.");
	    	    	RelvarSystem constraintsRelvar = (RelvarSystem)openGlobalRelvar(Catalog.relvarConstraints);
	    	    	constraintsRelvar.insertInternal(generator, constraintName, sourceCode, owner);
	    	    	addDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintOperator, references.getReferencedOperators());
	    	    	addDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintRelvar, references.getReferencedRelvars());    	
	    	    	addDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintType, references.getReferencedTypes());    	
	    			constraints.put(constraintName, operator);
	    			return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0343: createConstraint failed: " + t);    		
    	}
    }
    
    /** Drop constraint. */
    public synchronized void dropConstraint(final Generator generator, final String constraintName) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    	    	RelvarSystem constraintsRelvar = (RelvarSystem)openGlobalRelvar(Catalog.relvarConstraints);
	    	    	constraintsRelvar.deleteInternal(generator, constraintName);
	    	    	removeDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintOperator);
	    	    	removeDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintRelvar);
	    	    	removeDependencies(generator, constraintName, Catalog.relvarDependenciesConstraintType);
	    			constraints.remove(constraintName);
	    			return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0344: dropConstraint failed: " + t);    		
    	}
    }
    
    /** Return true if operator exists */
    public synchronized boolean isOperatorExists(OperatorSignature signature) {
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	return (operatorsRelvar.isOperatorExists(new Generator(RelDatabase.this, System.out), signature));
    }
     
    /** Return the name of the TYPE that created the given operator.  Return NULL if the operator is not found. */
    public synchronized String getOperatorGenerationTypeName(OperatorSignature signature) {
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	return (operatorsRelvar.getOperatorGenerationTypeName(new Generator(RelDatabase.this, System.out), signature));
    }
    
    /** Create operator. */
    public synchronized void createOperator(final Generator generator, final OperatorDefinition operator) {
		if (isOperatorExists(operator.getSignature()))
			throw new ExceptionSemantic("RS0216: OPERATOR " + operator.getSignature() + " is already in the database.");
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    	    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
	    	    	String opsig = operator.getSignature().toRelLookupString();
	    	    	String returnType = (operator.getDeclaredReturnType() != null) ? operator.getDeclaredReturnType().getSignature() : "";
	    	    	operatorsRelvar.insertInternal(new Generator(RelDatabase.this, System.out), operator.getSignature().getName(), returnType, opsig, operator.getSourceCode(), operator.getLanguage(), operator.getCreatedByType(), operator.getOwner());
	    	    	References references = operator.getReferences();
	    	    	// Remove self-reference
	    	    	references.removeReferenceToOperator(opsig);
	    	    	addDependencies(generator, opsig, Catalog.relvarDependenciesOperatorOperator, references.getReferencedOperators());
	    	    	addDependencies(generator, opsig, Catalog.relvarDependenciesOperatorRelvar, references.getReferencedRelvars());
	    	    	addDependencies(generator, opsig, Catalog.relvarDependenciesOperatorType, references.getReferencedTypes());
	    			return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0345: createOperator failed: " + t);    		
    	}
    }
    
    private void dropOperatorInternal(Generator generator, OperatorSignature signature) {
		if (operatorCache.containsKey(signature))
			operatorCache.remove(signature);
    	String opsig = signature.toRelLookupString();
    	StringBuffer dependencies = new StringBuffer();
    	obtainDependencies(generator, dependencies, opsig, Catalog.relvarDependenciesConstraintOperator, "CONSTRAINT");
    	obtainDependencies(generator, dependencies, opsig, Catalog.relvarDependenciesOperatorOperator, "OPERATOR");
    	obtainDependencies(generator, dependencies, opsig, Catalog.relvarDependenciesRelvarOperator, "VAR");
    	obtainDependencies(generator, dependencies, opsig, Catalog.relvarDependenciesTypeOperator, "TYPE");
    	if (dependencies.length() > 0)
    		throw new ExceptionSemantic("RS0217: Operator " + signature + " may not be dropped due to dependencies:" + dependencies);
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	operatorsRelvar.deleteInternal(generator, signature);
    	dirClassLoader.unload(getRelUserCodePackage() + "." + signature.getClassSignature());
    	removeDependencies(generator, opsig, Catalog.relvarDependenciesOperatorOperator);
    	removeDependencies(generator, opsig, Catalog.relvarDependenciesOperatorRelvar);
    	removeDependencies(generator, opsig, Catalog.relvarDependenciesOperatorType);    	
    }
    
    /** Drop operator. */
    public synchronized void dropOperator(final OperatorSignature signature) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			dropOperatorInternal(new Generator(RelDatabase.this, System.out), signature);
	    			return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0346: dropOperator failed: " + t);    		
    	}
    }

    public Class<?> loadClass(String classSignature) {
		try {
			return dirClassLoader.forName(getRelUserCodePackage() + "." + classSignature);
		} catch (ClassNotFoundException t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0347: Failed loading class " + classSignature + ": " + t.toString());
		}
    }
    
    public Method getMethod(OperatorSignature signature) {
        // Target class must have a 'static void execute(Context context)' method.
    	String classSignature = signature.getClassSignature();
		try { 
			return loadClass(classSignature).getDeclaredMethod("execute", new Class[] {Context.class});
		} catch (NoSuchMethodException m) {
    		m.printStackTrace();
			throw new ExceptionFatal("RS0348: Failed loading operator " + signature + " from class " + classSignature + ": " + m.toString());
		}
    }
	
	public void cacheOperator(OperatorDefinition op) {
		operatorCache.put(op.getSignature(), op);
	}
    
	private OperatorDefinition getOperatorFromCache(OperatorSignature signature) {
		OperatorDefinition definition = operatorCachePermanent.get(signature);
		if (definition != null)
			return definition;
		return operatorCache.get(signature);
	}
	
    /** Return true if an operator of the given signature has been loaded.  Return null if it can't be found. */
    public synchronized OperatorDefinition loadOperator(Generator generator, OperatorSignature signature) {
    	OperatorDefinition definition = getOperatorFromCache(signature);
    	if (definition != null)
    		return definition;
    	definition = builtinops.get(signature.toNativeLookupString());
    	if (definition != null)
    		return definition;
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	ValueTuple operatorTuple = operatorsRelvar.getTupleForKey(generator, signature.getName());
    	if (operatorTuple == null)
    		return null;    	
    	Interpreter interpreter = new Interpreter(RelDatabase.this, System.out);
		ValueRelationLiteral existingImplementations = (ValueRelationLiteral)operatorTuple.getValues()[1];
		TupleIterator implementationIterator = existingImplementations.iterator();
		try {
			while (implementationIterator.hasNext()) {
				ValueTuple implementationTuple = implementationIterator.next();
				String existingImplementationSignature = ((ValueCharacter)implementationTuple.getValues()[0]).stringValue();
				if (existingImplementationSignature.equalsIgnoreCase(signature.toRelLookupString())) {
			    	String sourceCode = implementationTuple.getValues()[2].toString();
			    	String language = implementationTuple.getValues()[3].toString();
			    	if (language.equals("Rel") || language.equals("System")) {
						try {
							if (sourceCode.length() == 0) {
								String createdByType = implementationTuple.getValues()[4].toString();
								loadType(generator, createdByType);
								return getOperatorFromCache(signature);
							} else
								return interpreter.compileOperator(sourceCode);
						} catch (ParseException pe) {
				    		pe.printStackTrace();
							throw new ExceptionFatal("RS0349: Failed loading operator " + signature + ": " + pe.toString());
						}
			    	} else if (language.equals("JavaP")) {
			    		return new OperatorDefinitionNativeProcedureExternal(signature, getMethod(signature));
			    	} else if (language.equals("JavaF")) {
			    		OperatorDefinitionNativeFunctionExternal opdef = new OperatorDefinitionNativeFunctionExternal(signature, getMethod(signature));
				    	org.reldb.rel.v0.types.Type returnType;
						try {
							returnType = interpreter.getOperatorReturnType(sourceCode);
						} catch (ParseException pe) {
				    		pe.printStackTrace();
							throw new ExceptionFatal("RS0350: Failed loading operator " + signature + ": " + pe.toString());
						}
			    		opdef.setDeclaredReturnType(returnType);
			    		return opdef;
			    	} else
			    		throw new ExceptionFatal("RS0351: Unrecognised language '" + language + "' in operator " + signature);				
				}
			}
		} finally {
			implementationIterator.close();
		}
		return null;    	
    }

	public synchronized void getPossibleTargetSignatures(HashSet<OperatorSignature> targets, Generator generator, OperatorSignature signature) {
    	RelvarOperators operatorsRelvar = (RelvarOperators)openGlobalRelvar(Catalog.relvarOperators);
    	ValueTuple operatorTuple = operatorsRelvar.getTupleForKey(generator, signature.getName());
    	if (operatorTuple != null) {
			ValueRelationLiteral existingImplementations = (ValueRelationLiteral)operatorTuple.getValues()[1];
			TupleIterator implementationIterator = existingImplementations.iterator();
			try {
		    	Interpreter interpreter = new Interpreter(RelDatabase.this, System.out);
				while (implementationIterator.hasNext()) {
					ValueTuple implementationTuple = implementationIterator.next();
					String parmSignature = ((ValueCharacter)implementationTuple.getValues()[0]).stringValue();
					String returnStr = ((ValueCharacter)implementationTuple.getValues()[1]).stringValue();
					String existingImplementationSignatureText = parmSignature + ((returnStr.length() > 0) ? " RETURNS " + returnStr : "");
			    	OperatorSignature storedSignature;
					try {
						storedSignature = interpreter.getOperatorSignature(existingImplementationSignatureText);
					} catch (ParseException pe) {
			    		pe.printStackTrace();
						throw new ExceptionFatal("RS0352: Unable to retrieve operator signature for '" + existingImplementationSignatureText + "': " + pe.toString());
					}
					if (storedSignature.canBeInvokedBy(signature))
						targets.add(storedSignature);
				}
			} finally {
				implementationIterator.close();
			}
    	}
		for (OperatorDefinition op: operatorCache.values()) {
			OperatorSignature storedSignature = op.getSignature();
			if (storedSignature.canBeInvokedBy(signature))
				targets.add(storedSignature);
		}
    	OperatorDefinition definition = builtinops.get(signature.toNativeLookupString());
    	if (definition != null)
    		targets.add(definition.getSignature());
	}
    
    synchronized void defineBuiltinOperator(OperatorDefinition operator) {
    	builtinops.put(operator.getSignature().toNativeLookupString(), operator);
    }
    
    // Return list of built-in operators as RELATION {signature CHAR}
    public synchronized ValueRelation getBuiltinOperators(Generator generator) {
    	return new ValueRelation(generator) {
    		
			private static final long serialVersionUID = 1L;

			public int hashCode() {
				return 0;
			}
			
			public TupleIterator newIterator() {
				return new TupleIterator() {
					Iterator<OperatorDefinition> i = builtinops.values().iterator();
					public boolean hasNext() {
						return i.hasNext();
					}
					public ValueTuple next() {
						Generator generator = getGenerator();
						return new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, i.next().toString())});
					}
					public void close() {}
				};
			}
    	};
    }
    
    /** Return true if a relvar exists. */
    public synchronized boolean isRelvarExists(final String name) {
    	try {
	    	return ((Boolean)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    				return new Boolean(isRelvarExists(txn, name));
	    		}
	    	}).execute(this)).booleanValue();
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0353: isRelvarExists failed: " + t);
    	}
    }
    
    /** Get the table for a given real relvar name. */
    public synchronized KeyTables getTable(Transaction txn, String name) throws DatabaseException {
		RelvarMetadata metadata = getRelvarMetadata(txn, name);
		if (metadata == null)
			return null;
		if (!(metadata instanceof RelvarRealMetadata))
			throw new ExceptionFatal("RS0354: VAR " + name + " is not a REAL relvar.");
		KeyTableNames tableName = ((RelvarRealMetadata)metadata).getTableName();
		KeyTables table = new KeyTables(tableName.size());
		for (int i=0; i<tableName.size(); i++) {
			String tabName = tableName.getName(i);
			Database db = openDatabase(txn, tabName, dbConfigurationNormal);
			table.setDatabase(i, db);
		}
		return table;
    }

    // Retrieve a global Relvar's metadata.  Return null if not found.
    public RelvarMetadata getRelvarMetadata(final String relvarName) {
    	try {
	    	return ((RelvarMetadata)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    				return getRelvarMetadata(txn, relvarName);
	    		}
	    	}).execute(this));
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0355: getRelvarMetadata failed: " + t);
    	}
    }
    
    String getUniqueTableName() {
    	return "relvar_" + getUniqueID();
    }
	
    /** Create a real relvar with specified metadata. */
    public synchronized void createRealRelvar(final Generator generator, final RelvarDefinition relvarInfo) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    		    	if (isRelvarExists(txn, relvarInfo.getName()))
    					throw new ExceptionSemantic("RS0218: VAR " + relvarInfo.getName() + " already exists.");
    		    	RelvarMetadata metadata = getRelvarMetadata(txn, relvarInfo.getName());
    		    	if (metadata != null && metadata instanceof RelvarRealMetadata) {
    		    		KeyTableNames tableName = ((RelvarRealMetadata)metadata).getTableName();
    		    		for (int i=0; i<tableName.size(); i++) {
    		    			String tabName = tableName.getName(i);
    		    			closeDatabase(tabName);
		    		    	try {
		    		    		environment.removeDatabase(txn, tabName);
		    		    	} catch (DatabaseException dbe) {
		    		    		dbe.printStackTrace();
		    		    		throw new ExceptionFatal("RS0356: unable to remove table " + tableName);
		    		    	}
    		    		}
    		    	}
    		    	RelvarRealMetadata newMetadata = (RelvarRealMetadata)relvarInfo.getRelvarMetadata(); 
    		    	KeyTableNames tableName = new KeyTableNames(newMetadata.getHeadingDefinition(RelDatabase.this).getKeyCount());
    		    	for (int i=0; i<tableName.size(); i++) {
    		    		String tabName = getUniqueTableName();
    		    		tableName.setName(i, tabName);
        		    	openDatabase(txn, tabName, dbConfigurationAllowCreate).close();
	    				openTables.remove(tabName);
    		    	}
   		    		newMetadata.setTableName(tableName);
    	    		putRelvarMetadata(txn, relvarInfo.getName(), newMetadata);
    	        	addDependencies(generator, relvarInfo.getName(), Catalog.relvarDependenciesRelvarType, relvarInfo.getReferences().getReferencedTypes());
    	    		return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0357: createRealRelvar failed: " + t);
    	}
    }

    /** Create a VIRTUAL relvar. */
	public synchronized void createVirtualRelvar(final Generator generator, final RelvarDefinition information) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    		    	if (isRelvarExists(txn, information.getName()))
    					throw new ExceptionSemantic("RS0219: VAR " + information.getName() + " already exists.");
    	    		putRelvarMetadata(txn, information.getName(), information.getRelvarMetadata());
    	        	addDependencies(generator, information.getName(), Catalog.relvarDependenciesRelvarOperator, information.getReferences().getReferencedOperators());
    	        	addDependencies(generator, information.getName(), Catalog.relvarDependenciesRelvarRelvar, information.getReferences().getReferencedRelvars());
    	        	addDependencies(generator, information.getName(), Catalog.relvarDependenciesRelvarType, information.getReferences().getReferencedTypes());
    	        	return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0358: createVirtualRelvar failed: " + t);
    	}		
	}

	public void createExternalRelvar(final Generator generator, final RelvarDefinition relvarInfo) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    		    	if (isRelvarExists(txn, relvarInfo.getName()))
    					throw new ExceptionSemantic("RS0220: VAR " + relvarInfo.getName() + " already exists.");
    	    		putRelvarMetadata(txn, relvarInfo.getName(), relvarInfo.getRelvarMetadata());
    	        	addDependencies(generator, relvarInfo.getName(), Catalog.relvarDependenciesRelvarType, relvarInfo.getReferences().getReferencedTypes());
    	    		return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0359: createExternalRelvar failed: " + t);
    	}
	}

	/** Get new temporary storage area. */
	private synchronized Database createTempStorage(final DatabaseConfig config) {
		String newTableName = getUniqueTableName();
		Transaction txn = null;
		Database database = openDatabase(txn, newTableName, config);
		tempTables.add(newTableName);
    	return database;
	}	
	
	/** Get new temporary storage area. */
	public Database createTempStorage() {
		return createTempStorage(dbConfigurationTemporary); 
	}
	
	/** Get new temporary storage area.  Does not use custom comparator. */
	public Database createTempStorageWithDuplicatesNoComparator() {
		return createTempStorage(dbConfigurationTemporaryWithDuplicatesNoComparator); 
	}
	
	/** Close temp storage */ 
	public synchronized void destroyTempStorage(Database temp)  {
		String tableName = temp.getDatabaseName();
		closeDatabase(tableName);
		tempTables.remove(tableName);
	}
	
	/** Get new 'private' (temporary) Table. */
	public synchronized TablePrivate getTempTable(final RelvarHeading keyDefinition) {
    	try {
	    	return (TablePrivate)((new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			KeyTables tables = new KeyTables(keyDefinition.getKeyCount());
	    			for (int i=0; i<tables.size(); i++) {
	    				String newTableName = getUniqueTableName();
	    				Database db = openDatabase(txn, newTableName, dbConfigurationAllowCreate);
	    				tempTables.add(newTableName);
	    				tables.setDatabase(i, db);
	    			}
	    	    	return new TablePrivate(RelDatabase.this, tables, keyDefinition);
	    		}
	    	}).execute(this));
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0360: getTempTable failed: " + t);
    	}
	}
	
	/** Create a PRIVATE relvar. */
	public synchronized SlotScoped createPrivateRelvar(final int depth, final int offset, final RelvarHeading keydef) {
    	return new RelvarPrivate(depth, offset, this, keydef);
	}
	
    /** Open a global relvar.  Return null if it doesn't exist. */
    public synchronized RelvarGlobal openGlobalRelvar(final String name) {
    	try {
	    	return ((RelvarGlobal)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	        		RelvarMetadata metadata = getRelvarMetadata(txn, name);
	        		if (metadata == null)
    	    			return null;
	        		return metadata.getRelvar(name, RelDatabase.this);
	    		}
	    	}).execute(this));
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0361: openRelvar failed: " + t);
    	}
    }
    
    /** Drop a relvar.  Throw an exception if it doesn't exist. */
    public synchronized void dropRelvar(final String name) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
    		    	RelvarMetadata metadata = getRelvarMetadata(txn, name);
    				if (metadata == null)
    					throw new ExceptionSemantic("RS0221: VAR " + name + " does not exist.");
    				Generator generator = new Generator(RelDatabase.this, System.out);
    		    	StringBuffer dependencies = new StringBuffer();
    		    	obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesRelvarRelvar, "VAR");
    		    	obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesOperatorRelvar, "OPERATOR");
    		    	obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesConstraintRelvar, "CONSTRAINT");
    		    	obtainDependencies(generator, dependencies, name, Catalog.relvarDependenciesTypeRelvar, "TYPE");
    		    	if (dependencies.length() > 0)
    		    		throw new ExceptionSemantic("RS0222: VAR " + name + " may not be dropped due to dependencies:" + dependencies);
    				metadata.dropRelvar(RelDatabase.this);
    				if (metadata instanceof RelvarRealMetadata) {
    		    		KeyTableNames tableName = ((RelvarRealMetadata)metadata).getTableName();
    		    		for (int i=0; i<tableName.size(); i++) {
    		    			String tabName = tableName.getName(i);
    		    			closeDatabase(tabName);
    		    	    	environment.removeDatabase(txn, tabName);
    		    		}    	
    				}
    				dropRelvarMetadata(txn, name);
    	        	removeDependencies(generator, name, Catalog.relvarDependenciesRelvarOperator);
    	        	removeDependencies(generator, name, Catalog.relvarDependenciesRelvarRelvar);
    	        	removeDependencies(generator, name, Catalog.relvarDependenciesRelvarType);
    	    		return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0362: dropRelvar failed: " + t);
    	}
    }

    // Copy tuples from a ValueRelation to a Table
    private void copy(Generator generator, Transaction txn, Table table, KeyTables db, ValueRelation source) throws DatabaseException {
    	TupleIterator iterator = ((ValueRelation)source.getSerializableClone()).iterator();
    	try {
    		while (iterator.hasNext())
				table.insertTupleNoDuplicates(generator, db, txn, iterator.next(), "Assigning");    			
    	} finally {
    		iterator.close();
    	}
    }
    
    // Set the value of a given Relvar
    public synchronized void setValue(final RelvarPrivateCell target, final ValueRelation source) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			KeyTableNames newTableName = new KeyTableNames(target.getTable().getHeadingDefinition().getKeyCount());
	    			KeyTables newDb = new KeyTables(newTableName.size());
	    			for (int i=0; i<newTableName.size(); i++) {
	    				String tabName = getUniqueTableName();
	    				newTableName.setName(i, tabName);
	    		    	Database db = openDatabase(txn, tabName, dbConfigurationAllowCreate);
		    	    	tempTables.add(tabName);
		    	    	newDb.setDatabase(i, db);
	    			}
	    	    	copy(new Generator(RelDatabase.this, System.out), txn, target.getTable(), newDb, source);
	    			KeyTables oldDb = target.getTable().getTable(txn);
	    			for (int i=0; i<oldDb.size(); i++) {
	    				Database olddb = oldDb.getDatabase(i);
		    			String tabName = olddb.getDatabaseName();
	    				openTables.remove(tabName);
	    				tempTables.remove(tabName);
	    				olddb.close();
	    				environment.removeDatabase(txn, tabName);	    				
	    			}
    				target.setTable(newDb);
    	    		return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0363: RelvarPrivate setValue failed: " + t);
    	}    	    	
    }
    
    // Set the value of a given Relvar
    public synchronized void setValue(final RelvarReal target, final ValueRelation source) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			KeyTables oldDb = getTable(txn, target.getName());
	    			KeyTableNames newTableName = new KeyTableNames(oldDb.size());
	    			KeyTables db = new KeyTables(oldDb.size());
	    			for (int i=0; i<newTableName.size(); i++) {
	    				String tabName = getUniqueTableName();
	    				newTableName.setName(i, tabName);
	    		    	Database dbase = openDatabase(txn, tabName, dbConfigurationAllowCreate);
	    		    	db.setDatabase(i, dbase);
	    			}
	    	    	copy(new Generator(RelDatabase.this, System.out), txn, target.getTable(), db, source);
	    			KeyTableNames oldTableName = oldDb.getDatabaseName();
	    			for (int i=0; i<oldTableName.size(); i++) {
	    				String tabName = oldTableName.getName(i);
	    				openTables.remove(tabName);
	    				oldDb.getDatabase(i).close();
	    				environment.removeDatabase(txn, tabName);	    				
	    			}
    		    	RelvarRealMetadata metadata = (RelvarRealMetadata)getRelvarMetadata(txn, target.getName());
    		    	metadata.setTableName(newTableName);
    	    		putRelvarMetadata(txn, target.getName(), metadata);
    	    		return null;
	    		}
	    	}).execute(this);
    	} catch (ExceptionSemantic es) {
    		throw es;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0364: RelvarReal setValue failed: " + t);
    	}    	
    }
    
}
