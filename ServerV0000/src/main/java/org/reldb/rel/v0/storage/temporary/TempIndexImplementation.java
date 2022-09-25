package org.reldb.rel.v0.storage.temporary;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.ValueTuple;

public class TempIndexImplementation implements TempIndex {
	
	// TODO - this should either be tunable or dynamic
	private static final long storageMemoryLimit = 100;
	
	private TempIndex storageImplementation;
	private long storageCount = 0;
	private RelDatabase database;
	
	public TempIndexImplementation(RelDatabase db) {
		database = db;
		storageImplementation = new TempIndexMemory();
	}
	
	/* (non-Javadoc)
	 * @see org.reldb.rel.v0.storage.TempStorageTuplesInterface#close()
	 */
	@Override
	public void close() {
		storageImplementation.close();
	}

	@Override
	public void put(ValueTuple keyTuple, ValueTuple valueTuple) {
		storageImplementation.put(keyTuple, valueTuple);
		storageCount++;
		if (storageCount == storageMemoryLimit) {
			TempIndexDisk target = new TempIndexDisk(database);
			Iterator<Entry<ValueTuple, LinkedList<ValueTuple>>> source = ((TempIndexMemory)storageImplementation).iterator();
			while (source.hasNext()) {
				Entry<ValueTuple, LinkedList<ValueTuple>> entry = source.next(); 
				LinkedList<ValueTuple> values = entry.getValue();
				Iterator<ValueTuple> valueIterator = values.iterator();
				while (valueIterator.hasNext())
					target.put(entry.getKey(), valueIterator.next());
			}
			storageImplementation.close();
			storageImplementation = target;
		}
	}
	
	// Get a TupleIterator on values which iterates all values for a given Key
	/* (non-Javadoc)
	 * @see org.reldb.rel.v0.storage.TempStorageTuplesInterface#keySearch(org.reldb.rel.v0.generator.Generator, org.reldb.rel.v0.values.ValueTuple)
	 */
	@Override
	public TupleIterator keySearch(ValueTuple key) {
		return storageImplementation.keySearch(key);
	}

}
