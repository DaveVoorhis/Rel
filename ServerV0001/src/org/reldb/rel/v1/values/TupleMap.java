package org.reldb.rel.v1.values;

/** Functor object for converting one tuple to another. */
public abstract class TupleMap {
	public abstract ValueTuple map(ValueTuple tuple);
}
