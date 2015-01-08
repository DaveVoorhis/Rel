package org.reldb.rel.v0.values;

/** Functor object for performing a fold. */
public abstract class TupleFold extends TupleIteration {
	
	private Value accumulator = null;
	private int attributeIndex;
	
	public TupleFold(TupleIterator iterator, int attributeIndex) {
		super(iterator);
		this.attributeIndex = attributeIndex;
	}
	
	public void process(ValueTuple tuple) {
		accumulator = fold(accumulator, tuple.getValues()[attributeIndex]);
	}
	
	public void initialise() {
		accumulator = getIdentity();
	}
	
	public int getAttributeIndex() {
		return attributeIndex;
	}
	
	public Value getResult() {
		return accumulator;
	}
	
	public abstract Value fold(Value left, Value right);
	
	public abstract Value getIdentity();
	
}
