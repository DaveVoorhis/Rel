package org.reldb.rel.v0.values;

import java.io.Closeable;
import java.util.Iterator;

public abstract class TupleIterator implements Iterator<ValueTuple>, Closeable, AutoCloseable {
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public abstract boolean hasNext();
	
	public abstract ValueTuple next();
	
	public abstract void close();
}
