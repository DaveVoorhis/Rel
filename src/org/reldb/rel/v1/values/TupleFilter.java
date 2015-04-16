package org.reldb.rel.v1.values;

/** Functor object for testing a tuple against some condition. */
public abstract class TupleFilter {
	public abstract boolean filter(ValueTuple tuple);
}
