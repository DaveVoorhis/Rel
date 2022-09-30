package org.reldb.rel.v0.values;

import org.reldb.rel.v0.generator.Generator;

import java.util.Arrays;

/** A TupleIteratorDupCount is a TupleIterator that generates an integer count of duplicates in the first attribute. */
public class TupleIteratorDupCount extends TupleIterator {
	private Generator generator;
	private TupleIterator source;

	public TupleIteratorDupCount(TupleIterator source, Generator generator) {
		this.generator = generator;
		this.source = source;
	}
	
	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public ValueTuple next() {
		ValueTuple tuple = source.next();
		long dupCount = Long.parseLong(tuple.getValues()[0].stringValue());
		Value[] dupCountRaw = new Value[] {ValueInteger.select(generator, dupCount)};
		Value[] dataTupleData = Arrays.copyOfRange(tuple.getValues(), 1, tuple.getValues().length);
		ValueTuple dataTuple = new ValueTuple(generator, dataTupleData);
		return (new ValueTuple(generator, dupCountRaw)).joinDisjoint(dataTuple);
	}

	@Override
	public void close() {
		source.close();
	}
}
