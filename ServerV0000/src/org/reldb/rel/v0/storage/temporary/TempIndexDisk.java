package org.reldb.rel.v0.storage.temporary;

import java.util.NoSuchElementException;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.ValueTuple;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

// As of Oracle Berkeley Java DB version 5.0.34, use of cursor.getSearchKey() on a 'database' with
// a defined Comparator (of type RelDatabase.ComparisonHandler) resulted in 
// 'StreamCorruptedException: invalid type code: AC' in RelDatabase.ComparisonHandler.  This appears to
// have something to do with using setSortedDuplicates(true) in the DatabaseConfig along with
// a user-defined Comparator.
//
// To get around this, a Database called keys holds <key: key, value: id> pairs, whilst a 
// Database called values holds <key: id, value: valueTuple> pairs.  The former identifies
// key values, the latter holds associated tuple values.
//
// storageKeys requires unique keys and the custom Comparator RelDatabase.ComparisonHandler
// storageValues requires non-unique keys and no custom Comparator
//
class TempIndexDisk implements TempIndex {

	private Database keys;
	private Database values;
	private RelDatabase database;
	private Long id = Long.valueOf(0);
	
	public TempIndexDisk(RelDatabase db) {
		database = db;
		keys = database.createTempStorage();
		values = database.createTempStorageWithDuplicatesNoComparator();
	}
	
	public void close() {
		database.destroyTempStorage(keys);
		database.destroyTempStorage(values);
	}
	
	public void put(ValueTuple keyTuple, ValueTuple valueTuple) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(keyTuple, theKey);

		DatabaseEntry theID = new DatabaseEntry();
		
		OperationStatus status = keys.get(null, theKey, theID, LockMode.DEFAULT);
		if (status == OperationStatus.NOTFOUND) {
			LongBinding.longToEntry(id++, theID);
			keys.put(null, theKey, theID);
		} else if (status != OperationStatus.SUCCESS)
			throw new ExceptionFatal("RS0318: Insertion failure in put().");
		
		DatabaseEntry theData = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(valueTuple, theData);
		
		values.put(null, theID, theData);
	}
	
	// Get a TupleIterator on values which iterates all values for a given Key
	public TupleIterator keySearch(ValueTuple keyTuple) {
		DatabaseEntry theKey = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(keyTuple, theKey);

		final DatabaseEntry theID = new DatabaseEntry();
		
		OperationStatus status = keys.get(null, theKey, theID, LockMode.DEFAULT);
		if (status == OperationStatus.SUCCESS) {
		    return new TupleIterator() {
				protected Cursor cursor = null;
			    protected ValueTuple current = null;
			    
				public ValueTuple next() {
					if (hasNext())
						try {
							return current;
						} finally {
							current = null;
						}
					throw new NoSuchElementException();
				}
				
				public void close() {
					try {
						if (cursor != null) {
							cursor.close();
						}
					} catch (DatabaseException exp) {
						throw new ExceptionFatal("RS0319: Unable to close cursor: " + exp.getMessage());
					}
				}
				
		    	public boolean hasNext() {
		    		if (current != null)
		    			return true;
		    		try {
					    final DatabaseEntry foundData = new DatabaseEntry();	
		    			if (cursor == null) {
		    				cursor = values.openCursor(null, null);
		    				if (cursor.getSearchKey(theID, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			    				current = (ValueTuple)database.getTupleBinding().entryToObject(foundData); 
		    					return true;
		    				}
		    				return false;
		    			} else if (cursor.getNextDup(theID, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
		    				current = (ValueTuple)database.getTupleBinding().entryToObject(foundData); 
		    				return true;
		    			}
		    		} catch (DatabaseException exp) {
		    			throw new ExceptionFatal("RS0320: Unable to get next tuple: " + exp.getMessage());					
		    		}
		    		return false;
		    	}	
			};		
		} else {
			return new TupleIterator() {
				public boolean hasNext() {
					return false;
				}
				public ValueTuple next() {
					return null;
				}
				public void close() {}				
			};
		}			
	}
	
}
