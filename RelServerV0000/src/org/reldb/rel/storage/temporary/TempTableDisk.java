package org.reldb.rel.storage.temporary;

import java.util.NoSuchElementException;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.values.TupleIterator;
import org.reldb.rel.values.ValueTuple;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

class TempTableDisk implements TempTable {

	private Database storage;
	private RelDatabase database;
	
	public TempTableDisk(RelDatabase db) {
		database = db;
		storage = database.createTempStorage();
	}
	
	public void close() {
		database.destroyTempStorage(storage);
	}
	
	public void put(ValueTuple tuple) throws DatabaseException {
		DatabaseEntry theKey = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(tuple, theKey);
		storage.put(null, theKey, database.getKeyTableEntry());
	}

	private class StorageIterator extends TupleIterator {
		protected Cursor cursor = null;
	    protected ValueTuple current = null;
	    
		public boolean hasNext() {
			if (current != null)
				return true;
			try {
				if (cursor == null)
					cursor = storage.openCursor(null, null);
			    DatabaseEntry foundKey = new DatabaseEntry();
			    DatabaseEntry foundData = new DatabaseEntry();	
				if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					current = (ValueTuple)database.getTupleBinding().entryToObject(foundKey); 
					return true;
				}
			} catch (DatabaseException exp) {
				throw new ExceptionFatal("RS0321: Unable to get next tuple: " + exp.getMessage());					
			}
			return false;
		}
		
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
				if (cursor != null)
					cursor.close();
			} catch (DatabaseException exp) {
				throw new ExceptionFatal("RS0322: Unable to close cursor: " + exp.getMessage());
			}
		}		
	}
	
	// Get a TupleIterator on values
	public TupleIterator values() {
	    return new StorageIterator();
	}
	
}
