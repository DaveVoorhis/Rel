package org.reldb.rel.storage.temporary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.reldb.rel.values.TupleIterator;
import org.reldb.rel.values.ValueTuple;

// Memory-based temporary tuple storage.
class TempIndexMemory implements TempIndex {
	
	private HashMap<ValueTuple, LinkedList<ValueTuple>> storage;
	
	Iterator<Entry<ValueTuple, LinkedList<ValueTuple>>> iterator() {
		return storage.entrySet().iterator();
	}
	
	public TempIndexMemory() {
		storage = new HashMap<ValueTuple, LinkedList<ValueTuple>>();
	}
	
	public void close() {
		storage = null;
	}
	
	public void put(ValueTuple keyTuple, ValueTuple valueTuple) {
		LinkedList<ValueTuple> values = storage.get(keyTuple);
		if (values == null) {
			values = new LinkedList<ValueTuple>();
			storage.put(keyTuple, values);
		}
		values.add(valueTuple);
	}
	
	@Override
	// Get a TupleIterator on values which iterates all values for a given Key
	public TupleIterator keySearch(final ValueTuple key) {
		return new TupleIterator() {
			LinkedList<ValueTuple> list = storage.get(key);
			Iterator<ValueTuple> listIterator;
			@Override
			public boolean hasNext() {
				if (list == null)
					return false;
				if (listIterator == null)
					listIterator = list.iterator();
				return listIterator.hasNext();
			}
			@Override
			public ValueTuple next() {
				if (!hasNext())
					return null;
				return listIterator.next();
			}
			@Override
			public void close() {
			}
		};
	}
	
}
