package org.reldb.rel.v0.values;

import java.util.HashSet;
import java.util.NoSuchElementException;

import org.reldb.rel.exceptions.ExceptionSemantic;

/** A TupleIteratorUnique is a TupleIterator that throws an exception if it encounters duplicate ValueTuple's. */
public class TupleIteratorDisjoint extends TupleIterator {

	private TupleIterator source;
	private HashSet<ValueTuple> tupleSet;
	
	// TODO - MEM - modify TupleIteratorDisjoint to prevent out-of-memory on high-cardinality relations
	public TupleIteratorDisjoint(TupleIterator source) {
		this.source = source;
		tupleSet = new HashSet<ValueTuple>();
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public ValueTuple next() {
		if (hasNext()) {
			ValueTuple tuple = source.next();
			if (tupleSet.contains(tuple))
				throw new ExceptionSemantic("RS0264: Requirement that tuples be disjoint has been violated.");
			tupleSet.add(tuple);
			return tuple;
		}
		throw new NoSuchElementException();
	}

	public void close() {
		source.close();
	}
}
