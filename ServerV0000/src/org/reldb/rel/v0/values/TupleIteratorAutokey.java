package org.reldb.rel.v0.values;

import org.reldb.rel.v0.generator.Generator;

/** A TupleIteratorAutokey is a TupleIterator that generates a unique auto-numbered attribute. */
public class TupleIteratorAutokey extends TupleIterator {
	private Generator generator;
	private TupleIterator source;
	private long autokey;
	
	public TupleIteratorAutokey(TupleIterator source, Generator generator) {
		this.generator = generator;
		this.source = source;
		autokey = 1;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public ValueTuple next() {
		Value[] autoKeyTupleRaw = new Value[] {ValueInteger.select(generator, autokey++)};
		return (new ValueTuple(generator, autoKeyTupleRaw)).joinDisjoint(source.next());
	}

	@Override
	public void close() {
		source.close();
	}
}
