package org.reldb.rel.storage.tables;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.storage.relvars.RelvarExternal;
import org.reldb.rel.values.RelTupleFilter;
import org.reldb.rel.values.RelTupleMap;
import org.reldb.rel.values.TupleFilter;
import org.reldb.rel.values.TupleIterator;
import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.values.ValueTuple;
import org.reldb.rel.vm.Context;

public interface TableExternal {
	
	public enum DuplicateHandling {
		DUP_COUNT,
		AUTOKEY,
		DUP_REMOVE
	}
	
	public TupleIterator iterator();

	public TupleIterator iterator(Generator generator);
	
	public long getCardinality();
	
	public boolean contains(Generator generator, ValueTuple tuple);

	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple);

	public void setValue(RelvarExternal relvarCSV, ValueRelation relation);

	public long insert(Generator generator, ValueRelation relation);

	public long insert(Generator generator, ValueTuple tuple);
	
	public long insertNoDuplicates(Generator generator, ValueRelation relation);
	
	public void purge();

	public void delete(Generator generator, ValueTuple tuple);

	public long delete(Generator generator, RelTupleFilter relTupleFilter);

	public long delete(Generator generator, TupleFilter filter);

	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded);

	public long update(Generator generator, RelTupleMap relTupleMap);

	public long update(Generator generator, RelTupleFilter relTupleFilter, RelTupleMap relTupleMap);
}
