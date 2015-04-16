package org.reldb.rel.v1.storage.temporary;

import java.util.Iterator;

import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.values.TupleIterator;
import org.reldb.rel.v1.values.ValueTuple;

public class TempTableImplementation implements TempTable {
	
	// TODO - this should either be tunable or dynamic
	private static final long storageMemoryLimit = 100;
	
	private TempTable storageImplementation;
	private long storageCount = 0;
	private RelDatabase database;
	
	public TempTableImplementation(RelDatabase db) {
		database = db;
		storageImplementation = new TempTableMemory();
	}
	
	/* (non-Javadoc)
	 * @see org.reldb.rel.v0.storage.TempStorageTuplesInterface#close()
	 */
	@Override
	public void close() {
		storageImplementation.close();
	}
	
	@Override
	public void put(ValueTuple dataTuple) {
		storageImplementation.put(dataTuple);
		storageCount++;
		if (storageCount == storageMemoryLimit) {
			TempTable target = new TempTableDisk(database);
			Iterator<ValueTuple> source = ((TempTableMemory)storageImplementation).iterator();
			while (source.hasNext())
				target.put(source.next());
			storageImplementation.close();
			storageImplementation = target;
		}
	}
	
	// Get a TupleIterator on values
	/* (non-Javadoc)
	 * @see org.reldb.rel.v0.storage.TempStorageTuplesInterface#keys()
	 */
	@Override
	public TupleIterator values() {
		return storageImplementation.values();
	}

}
