package org.reldb.rel.v0.values;

import java.util.HashSet;
import java.util.NoSuchElementException;

/** A TupleIteratorUnique is a TupleIterator that removes all duplicate ValueTuple's. */
public class TupleIteratorUnique extends TupleIterator {

	private TupleIterator source;
	private HashSet<ValueTuple> tupleSet;
	private ValueTuple current;
	
	// TODO - MEM - modify TupleIteratorUnique to prevent out-of-memory on high-cardinality relations
	public TupleIteratorUnique(TupleIterator source) {
		this.source = source;
		tupleSet = new HashSet<ValueTuple>();
		current = null;
	}
	
	@Override
	public boolean hasNext() {
		if (current != null)
			return true;
		do {
			if (!source.hasNext())
				return false;
			current = source.next();
		} while (tupleSet.contains(current));
		tupleSet.add(current);
		return true;
	}

	@Override
	public ValueTuple next() {
		if (hasNext())
			try {
				return current;
			} finally {
				current = null;
			}
		throw new NoSuchElementException();
	}

	public void close() {
		source.close();
	}
}
