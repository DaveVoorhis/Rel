package org.reldb.rel.storage;

import java.io.File;

import org.reldb.rel.external.DirClassLoader;
import org.reldb.rel.storage.relvars.RelvarMetadata;
import org.reldb.rel.values.ValueTuple;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class ClassCatalog {
    private StoredClassCatalog classCatalog;
    private Database classCatalogDb;
    private Environment environment;
    private EntryBinding<RelvarMetadata> relvarMetadataBinding;
    private SerialBinding<ValueTuple> tupleBinding;
    // class loader for external Java-based operators and types
    private DirClassLoader dirClassLoader;

    ClassCatalog(String directory, EnvironmentConfig environmentConfig, DatabaseConfig dbConfig) {
    	// This should be main database directory
    	dirClassLoader = new DirClassLoader(directory);
    	
        // Open the environment in subdirectory of the above
    	String classesDir = directory + java.io.File.separator + "classes";
    	RelDatabase.mkdir(classesDir);
        environment = new Environment(new File(classesDir), environmentConfig);
        
        // Open the class catalog db. This is used to optimize class serialization.
        classCatalogDb = environment.openDatabase(null, "_ClassCatalog", dbConfig);

        // Create our class catalog
        classCatalog = new StoredClassCatalog(classCatalogDb);
        
        // Need a serial binding for metadata
        relvarMetadataBinding = new SerialBinding<RelvarMetadata>(classCatalog, RelvarMetadata.class);
        
        // Need serial binding for data
        tupleBinding = new SerialBinding<ValueTuple>(classCatalog, ValueTuple.class) {
        	public ClassLoader getClassLoader() {
        		return dirClassLoader;
        	}
        };   	
    }

	public void close() throws DatabaseException {
    	classCatalogDb.close();
    	environment.close();
	}
    
    EntryBinding<RelvarMetadata> getRelvarMetadataBinding() {
    	return relvarMetadataBinding;
    }
    
    SerialBinding<ValueTuple> getTupleBinding() {
    	return tupleBinding;
    }

	public StoredClassCatalog getStoredClassCatalog() {
		return classCatalog;
	}
    
}
