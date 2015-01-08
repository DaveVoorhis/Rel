package org.reldb.rel.client;

public class NullTuple extends Tuple {

	/** This is the end-of-list Tuple in a set of TupleS. */
	public boolean isNull() {
		return true;
	}
	
}
