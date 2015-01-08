package org.reldb.rel.v0.values;

public abstract class TupleIteration {
	
	private long count = 0;
	private TupleIterator iterator;
	
	public TupleIteration(TupleIterator iterator) {
		this.iterator = iterator;
	}
	
	public ValueTuple next() {
		return iterator.next();
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}
	
	public void run() {
		try {
			initialise();
			while (hasNext()) {
				process(next());
				count++;
			}
		} finally {
			iterator.close();
		}
	}

	public void initialise() {}
	
	public long getCount() {
		return count;
	}
	
	public abstract void process(ValueTuple tuple);

}
