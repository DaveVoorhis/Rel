package org.reldb.rel.v1.storage.temporary;

import org.reldb.rel.v1.values.TupleIterator;
import org.reldb.rel.v1.values.ValueTuple;

/* Temporary storage of tuples. */
public interface TempTable {

	public abstract void close();

	public abstract void put(ValueTuple tuple);

	public abstract TupleIterator values();

}