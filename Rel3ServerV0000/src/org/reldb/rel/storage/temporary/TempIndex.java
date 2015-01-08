package org.reldb.rel.storage.temporary;

import org.reldb.rel.values.TupleIterator;
import org.reldb.rel.values.ValueTuple;

/* Temporary index.  Duplicate keys are allowed. */
public interface TempIndex {

	public abstract void close();
	
	public abstract void put(ValueTuple keyTuple, ValueTuple valueTuple);

	// Get a TupleIterator on values which iterates all values for a given Key
	public abstract TupleIterator keySearch(ValueTuple key);

}