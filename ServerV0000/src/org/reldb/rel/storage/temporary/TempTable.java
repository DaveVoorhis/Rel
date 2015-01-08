package org.reldb.rel.storage.temporary;

import org.reldb.rel.values.TupleIterator;
import org.reldb.rel.values.ValueTuple;

/* Temporary storage of tuples. */
public interface TempTable {

	public abstract void close();

	public abstract void put(ValueTuple tuple);

	public abstract TupleIterator values();

}