package org.reldb.rel.v0.storage.tables;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.RelTransaction;
import org.reldb.rel.v0.values.TupleIterator;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseException;

public abstract class RegisteredTupleIterator extends TupleIterator implements Comparable<RegisteredTupleIterator> {

	private static long iteratorIDGenerator = 0;
	
	private Long iteratorID;
	private RelDatabase database;
	
	protected Cursor cursor;
	protected RelTransaction txn = null;
	
	public RegisteredTupleIterator(RelDatabase database) {
		iteratorID = iteratorIDGenerator++;
		this.database = database;
	    database.registerTupleIterator(this);
	}
	
	public int hashCode() {
		return iteratorID.hashCode();
	}
	
	public int compareTo(RegisteredTupleIterator iterator) {
		return iteratorID.compareTo(iteratorID);
	}
	
	public boolean forceClose() {
		try {
			if (cursor != null) {
				cursor.close();
				database.commitTransaction(txn);
				return true;
			}
			return false;
		} catch (DatabaseException exp) {
    		exp.printStackTrace();
			throw new ExceptionFatal("RS0378: Unable to close cursor: " + exp.getMessage());
		}		
	}
	
	public void close() {
		try {
			forceClose();
		} finally {
			database.unregisterTupleIterator(this);
		}
	}
	
}