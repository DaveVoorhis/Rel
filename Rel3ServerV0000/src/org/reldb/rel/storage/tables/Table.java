package org.reldb.rel.storage.tables;

import com.sleepycat.je.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.generator.Generator;
import org.reldb.rel.generator.SelectAttributes;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.TransactionRunner;
import org.reldb.rel.storage.relvars.RelvarHeading;
import org.reldb.rel.storage.temporary.TempTable;
import org.reldb.rel.storage.temporary.TempTableImplementation;
import org.reldb.rel.types.AttributeMap;
import org.reldb.rel.types.Heading;
import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;

/** An updatable collection of ValueTupleS; a wrapper around Berkeley DB's "Database". */
public abstract class Table {

	private RelDatabase database;
	private RelvarHeading headingDefinition;
	private AttributeMap[] keyMaps;
	
	public Table(RelDatabase database, RelvarHeading headingDefinition) {
		this.database = database;
		this.headingDefinition = headingDefinition;
		keyMaps = new AttributeMap[headingDefinition.getKeyCount()];
		for (int keyNumber=0; keyNumber<headingDefinition.getKeyCount(); keyNumber++) {
			SelectAttributes keyAttributes = headingDefinition.getKey(keyNumber); 
			Heading sourceHeading = headingDefinition.getHeading();
			Heading targetHeading = sourceHeading.project(keyAttributes);
			keyMaps[keyNumber] = new AttributeMap(targetHeading, sourceHeading);
		}
	}
	
	public RelvarHeading getHeadingDefinition() {
		return headingDefinition;
	}
	
	protected abstract KeyTables getTable(Transaction txn) throws DatabaseException;
	
	public RelDatabase getDatabase() {
		return database;
	}
	
	private DatabaseEntry getKeyValueFromTuple(Generator generator, ValueTuple tuple, int keyNumber) {
		DatabaseEntry theKey = new DatabaseEntry();
		if (headingDefinition.getKeyCount() == 0)
			database.getTupleBinding().objectToEntry(tuple, theKey);
		else {
			ValueTuple keyTuple = keyMaps[keyNumber].project(generator, tuple);
			database.getTupleBinding().objectToEntry(keyTuple, theKey);
		}
		return theKey;
	}
	
	public boolean insertTupleNoDuplicates(Generator generator, KeyTables table, Transaction txn, ValueTuple tuple, String description) throws DatabaseException {
		DatabaseEntry theData = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(tuple, theData);
		// Put it in the database.
		for (int i=0; i<table.size(); i++) {
			Database tab = table.getDatabase(i);
			DatabaseEntry entry = (i == 0) ? theData : database.getKeyTableEntry();
			if (tab.putNoOverwrite(txn, getKeyValueFromTuple(generator, tuple, i), entry) == OperationStatus.KEYEXIST)
				throw new ExceptionSemantic("RS0232: " + description + " tuple would violate uniqueness constraint of KEY {" + headingDefinition.getKey(i) + "}");
		}
		return true;
	}

	boolean insertTuple(Generator generator, KeyTables table, Transaction txn, ValueTuple tuple, String description) throws DatabaseException {
		DatabaseEntry theData = new DatabaseEntry();
		database.getTupleBinding().objectToEntry(tuple, theData);
		// Put it in the database.  Skip it silently if duplicate.
		for (int i=0; i<table.size(); i++) {
			Database tab = table.getDatabase(i);
			DatabaseEntry entry = (i == 0) ? theData : database.getKeyTableEntry();
			if (tab.putNoOverwrite(txn, getKeyValueFromTuple(generator, tuple, i), entry) == OperationStatus.KEYEXIST)
				return false;
		}
		return true;
	}
	
	public void insert(final Generator generator, final ValueTuple tuple) {		
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			insertTupleNoDuplicates(generator, getTable(txn), txn, (ValueTuple)tuple.getSerializableClone(), "Inserting");
    	    		return null;
	    		}
	    	}).execute(database);
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
			throw new ExceptionSemantic("RS0233: insert tuple failed: " + t.getMessage());
    	}
	}

	private static abstract class Inserter {
		abstract boolean insert(Generator generator, KeyTables table, Transaction txn, ValueTuple tuple, String comment);
	}
	
	private long insert(final Generator generator, final ValueRelation relation, final Inserter inserter) {
    	try {
	    	return ((Long)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			// use of temporary storage prevents problems with deadlock or infinite iteration if we insert a relvar into itself
	    			// TODO - improve this so we only use temporary storage if there's the potential of inserting relvar 'x' (or a query involving relvar 'x') into itself.
	    	    	TempTable tmp = new TempTableImplementation(database);
	    			long insertCount = 0;
	    	    	try {
		    			KeyTables table = getTable(txn);
		    			TupleIterator iterator = relation.iterator();
		    			try {
		    				while (iterator.hasNext()) {
		    					ValueTuple tuple = (ValueTuple)iterator.next().getSerializableClone();
		    					tmp.put(tuple);
		    				}
		    			} finally {
		    				iterator.close();
		    			}
		    			iterator = tmp.values();
		    			try {
		    				while (iterator.hasNext()) {
				    			ValueTuple tuple = iterator.next();
		    					if (inserter.insert(generator, table, txn, tuple, "Inserting"))
		    						insertCount++;
		    				}
		    			} finally {
		    				iterator.close();
		    			}
	    	    	} finally {
	    	    		tmp.close();
	    	    	}
    	    		return new Long(insertCount);
	    		}
	    	}).execute(database)).longValue();
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionSemantic("RS0234: insert relation failed: " + t.getMessage());
    	}		
	}
	
	public long insert(final Generator generator, final ValueRelation relation) {
		return insert(generator, relation, new Inserter() {
			boolean insert(Generator generator, KeyTables table, Transaction txn, ValueTuple tuple, String comment) {
				return insertTuple(generator, table, txn, tuple, comment);
			}
		});
	}

	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		return insert(generator, relation, new Inserter() {
			boolean insert(Generator generator, KeyTables table, Transaction txn, ValueTuple tuple, String comment) {
				return insertTupleNoDuplicates(generator, table, txn, tuple, comment);
			}
		});
	}
	
	public long getCardinality() {
		try {
	    	return ((Long)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			return new Long(getTable(txn).getDatabase(0).count());
	    		}
	    	}).execute(database)).longValue();
    	} catch (ExceptionSemantic se) {
    		throw se;
		} catch (Throwable de) {
    		de.printStackTrace();
    		throw new ExceptionFatal("RS0370: getCardinality failed: " + de.getMessage());
		}
	}
	
	/** Obtain tuple value given a key.  Return null if not found. */
	public ValueTuple getTupleForKey(final Generator generator, final ValueTuple tuple) {
    	try {
	    	return ((ValueTuple)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    		    DatabaseEntry foundData = new DatabaseEntry();	
    				if (getTable(txn).getDatabase(0).get(txn, getKeyValueFromTuple(generator, tuple, 0), foundData, LockMode.READ_COMMITTED) == OperationStatus.SUCCESS)
						return (ValueTuple)database.getTupleBinding().entryToObject(foundData);
    				return null;
	    		}
	    	}).execute(database));
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0371: getTupleForKey failed: " + t.getMessage());
    	}		
	}
	
	public boolean contains(final Generator generator, final ValueTuple tuple) {
    	try {
	    	return ((Boolean)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    		    DatabaseEntry foundData = new DatabaseEntry();	
	    		    DatabaseEntry keyData = getKeyValueFromTuple(generator, tuple, 0);
    				return Boolean.valueOf(getTable(txn).getDatabase(0).get(txn, keyData, foundData, LockMode.READ_COMMITTED) == OperationStatus.SUCCESS);
	    		}
	    	}).execute(database)).booleanValue();
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
    		throw new ExceptionFatal("RS0372: contains failed: " + t.getMessage());
    	}
	}

	// Delete all tuples
	private void purge(Transaction txn) throws DatabaseException {
		KeyTables tables = getTable(txn);
		for (int i=0; i<tables.size(); i++) {
			Cursor cursor = tables.getDatabase(i).openCursor(txn, null);
			try {
			    DatabaseEntry foundKey = new DatabaseEntry();
			    DatabaseEntry foundData = new DatabaseEntry();	
				while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS)
					cursor.delete();
			} finally {
				cursor.close();
			}
		}
	}
	
	// Delete all tuples
	public void purge() {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			purge(txn);
    	    		return null;
	    		}
	    	}).execute(database);
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0373: purge failed: " + t.getMessage());
    	}
	}

	// Delete given tuple.
	public void delete(final Generator generator, final ValueTuple tuple) {
    	try {
	    	(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			KeyTables tables = getTable(txn);
	    			for (int i=0; i<tables.size(); i++)
	    				tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
    	    		return null;
	    		}
	    	}).execute(database);
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0374: delete tuple failed: " + t.getMessage());
    	}
	}
	
	// Delete selected tuples
	public long delete(final Generator generator, final TupleFilter filter) {
    	try {
	    	return ((Long)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			KeyTables tables = getTable(txn);
    				Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
    				long deleteCount = 0;
    				try {
    				    DatabaseEntry foundKey = new DatabaseEntry();
    				    DatabaseEntry foundData = new DatabaseEntry();	
    					while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
    						ValueTuple tuple = (ValueTuple)database.getTupleBinding().entryToObject(foundData);
    						tuple.loaded(generator);
    						if (filter.filter(tuple)) {
    							cursor.delete();
    							for (int i=1; i<tables.size(); i++)
    								tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
    							deleteCount++;
    						}
    					}
    				} finally {
    					cursor.close();
    				}
    	    		return new Long(deleteCount);
	    		}
	    	}).execute(database)).longValue();
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0375: delete tuples failed: " + t.getMessage());
    	}		
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.	
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		final HashMap<ValueTuple, Boolean> toDelete = new HashMap<ValueTuple, Boolean>();
		Generator generator = context.getGenerator();
		// index the tuplesToDelete into the toDelete index, which uses each tupleToDelete as a key and a Boolean as the value.
		TupleIterator iterator = tuplesToDelete.iterator();
		try {
			while (iterator.hasNext())
				toDelete.put(iterator.next(), Boolean.FALSE);
		} finally {
			iterator.close();
		}
		if (errorIfNotIncluded) {
			// make sure every tuple in toDelete is found in this relvar (table) at least once.
			TupleIterator relvarTupleIterator = iterator(generator);
			try {
				while (relvarTupleIterator.hasNext()) {
					ValueTuple keyTuple = relvarTupleIterator.next();
					if (toDelete.containsKey(keyTuple))
						toDelete.put(keyTuple, Boolean.TRUE);
				}
			} finally {
				relvarTupleIterator.close();
			}
			// make sure every entry in index is TRUE, i.e., has been found at least once
			Collection<Boolean>values = toDelete.values();
			Iterator<Boolean> valueIterator = values.iterator();
			while (valueIterator.hasNext())
				if (!valueIterator.next().booleanValue())
					throw new ExceptionSemantic("RS0235: In I_DELETE, one or more specified tuples are not included in the relvar.");
		}
		return delete(generator, new TupleFilter() {
			public boolean filter(ValueTuple tuple) {
				return toDelete.containsKey(tuple);
			}
		});
	}
	
	// Update selected tuples using a given TupleMap
	public long update(final Generator generator, final TupleFilter whereFilter, final TupleMap updateMap) {
    	try {
	    	return ((Long)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			TempTable insertionTemporaryTable = new TempTableImplementation(database);
	    			long updateCount = 0;
	    			try {
		    			KeyTables tables = getTable(txn);
	    			    DatabaseEntry foundKey = new DatabaseEntry();
	    			    DatabaseEntry foundData = new DatabaseEntry();	
		    			Cursor cursor = tables.getDatabase(0).openCursor(txn, null);
		    			try {
		    				while (cursor.getNext(foundKey, foundData, LockMode.RMW) == OperationStatus.SUCCESS) {
		    					ValueTuple tuple = (ValueTuple)database.getTupleBinding().entryToObject(foundData);
		    					tuple.loaded(generator);
		    					if (whereFilter.filter(tuple)) {
		    						ValueTuple newTuple = updateMap.map(tuple);
		    						cursor.delete();
		    						for (int i=1; i<tables.size(); i++)
		    							tables.getDatabase(i).delete(txn, getKeyValueFromTuple(generator, tuple, i));
		    						ValueTuple data = (ValueTuple)newTuple.getSerializableClone();
		    						insertionTemporaryTable.put(data);
									updateCount++;
		    					}
		    				}
		    			} finally {
		    				cursor.close();
		    			}		
		    			TupleIterator iterator = insertionTemporaryTable.values();
		    			try {
		    				while (iterator.hasNext())
		    					insertTupleNoDuplicates(generator, tables, txn, iterator.next(), "Updating");
		    			} finally {
		    				iterator.close();
		    			}
	    			} finally {
	    				insertionTemporaryTable.close();
	    			}
    	    		return new Long(updateCount);
	    		}
	    	}).execute(database)).longValue();
    	} catch (ExceptionSemantic se) {
    		throw se;
    	} catch (Throwable t) {
    		t.printStackTrace();
			throw new ExceptionFatal("RS0376: update failed: " + t.getMessage());
    	}		
	}
	
	// Update all tuples using a given TupleMap
	public long update(final Generator generator, final TupleMap map) {
		return update(generator, new TupleFilter() {
			public boolean filter(ValueTuple tuple) {
				return true;
			}
		}, map);
	}
		
	// Get a TupleIterator
	public TupleIterator iterator(final Generator generator) {
	    return new RegisteredTupleIterator(database) {
		    DatabaseEntry foundKey = new DatabaseEntry();
		    DatabaseEntry foundData = new DatabaseEntry();	
		    ValueTuple current = null;
		    boolean atEnd = false;
			public boolean hasNext() {
				if (current != null)
					return true;
				if (atEnd)
					return false;
				try {
					if (cursor == null) {
						txn = database.beginTransaction();
						cursor = getTable(txn.getTransaction()).getDatabase(0).openCursor(txn.getTransaction(), null);
					}
					if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
						current = (ValueTuple)database.getTupleBinding().entryToObject(foundData);
						current.loaded(generator);
						return true;
					} else
						atEnd = true;
				} catch (DatabaseException exp) {
		    		exp.printStackTrace();
					throw new ExceptionFatal("RS0377: Unable to get next tuple: " + exp.getMessage());					
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
		};
	}
		
}
