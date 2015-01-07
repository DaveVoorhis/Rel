package ca.mb.armchair.rel3.storage.tables;

import ca.mb.armchair.rel3.exceptions.ExceptionFatal;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.RelTransaction;
import ca.mb.armchair.rel3.values.TupleIterator;

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