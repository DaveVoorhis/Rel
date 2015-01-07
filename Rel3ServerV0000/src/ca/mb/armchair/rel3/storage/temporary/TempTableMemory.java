package ca.mb.armchair.rel3.storage.temporary;

import java.util.Iterator;
import java.util.LinkedList;

import ca.mb.armchair.rel3.values.TupleIterator;
import ca.mb.armchair.rel3.values.ValueTuple;

// Memory-based temporary tuple storage.
class TempTableMemory implements TempTable {
	
	private LinkedList<ValueTuple> storage;

	Iterator<ValueTuple> iterator() {
		return storage.iterator();
	}
	
	public TempTableMemory() {
		storage = new LinkedList<ValueTuple>();
	}
	
	public void close() {
		storage = null;
	}
	
	public void put(ValueTuple dataTuple) {
		storage.add(dataTuple);
	}
	
	// Get a TupleIterator on values
	public TupleIterator values() {
		return new TupleIterator() {
			Iterator<ValueTuple> keyIterator = storage.iterator();
			@Override
			public boolean hasNext() {
				return keyIterator.hasNext();
			}
			@Override
			public ValueTuple next() {
				return keyIterator.next();
			}
			@Override
			public void close() {
			}
		};
	}
	
}
