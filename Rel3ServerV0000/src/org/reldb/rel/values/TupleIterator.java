package org.reldb.rel.values;

import java.util.Iterator;

public abstract class TupleIterator implements Iterator<ValueTuple> {
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public abstract boolean hasNext();
	
	public abstract ValueTuple next();
	
	public abstract void close();
}
