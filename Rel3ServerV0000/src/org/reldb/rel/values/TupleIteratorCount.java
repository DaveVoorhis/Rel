package org.reldb.rel.values;

import java.util.ArrayList;

import org.reldb.rel.generator.Generator;

/** A TupleIteratorCount is a TupleIterator that counts the occurrences of tuples. */
public class TupleIteratorCount extends TupleIterator {

	private ArrayList<ValueTuple> tupleSet;
	private ValueTuple current;
	private Generator generator;
	private long count;
	private int index;
	
	public TupleIteratorCount(TupleIterator source, Generator generator) {
		this.generator = generator;
		tupleSet = new ArrayList<ValueTuple>();
		while (source.hasNext())
			tupleSet.add(source.next());
		source.close();
		count = 0;
		index = 0;
	}
	
	@Override
	public boolean hasNext() {
		if (index >= tupleSet.size())
			return false;
		
		current = tupleSet.get(index);
		index++;
		
		for(int i=0; i<tupleSet.size(); i++) {
			if (current.equals(tupleSet.get(i)))
				count++;
		}
		return true;
	}

	@Override
	public ValueTuple next() {
		try {
			return createCountTuple().joinDisjoint(current);
		} finally {
			current = null;
			count = 0;
		}
	}

	public void close() {
		index = 0;
	}
	
	private ValueTuple createCountTuple() {
		Value[] value = new Value[1];
		value[0] = ValueInteger.select(generator, count);
		return new ValueTuple(generator, value);
	}
}
