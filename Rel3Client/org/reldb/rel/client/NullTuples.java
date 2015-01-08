package org.reldb.rel.client;

/** Empty set of tuples */
public class NullTuples extends Tuples {

	public NullTuples() {
		super((Heading)null);
		insertNullTuple();
	}
	
}
