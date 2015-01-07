package ca.mb.armchair.rel3.storage.temporary;

import ca.mb.armchair.rel3.values.TupleIterator;
import ca.mb.armchair.rel3.values.ValueTuple;

/* Temporary storage of tuples. */
public interface TempTable {

	public abstract void close();

	public abstract void put(ValueTuple tuple);

	public abstract TupleIterator values();

}