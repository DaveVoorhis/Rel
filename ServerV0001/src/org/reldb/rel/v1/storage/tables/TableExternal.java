package org.reldb.rel.v1.storage.tables;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.relvars.RelvarExternal;
import org.reldb.rel.v1.values.RelTupleFilter;
import org.reldb.rel.v1.values.RelTupleMap;
import org.reldb.rel.v1.values.TupleFilter;
import org.reldb.rel.v1.values.TupleIterator;
import org.reldb.rel.v1.values.ValueRelation;
import org.reldb.rel.v1.values.ValueTuple;
import org.reldb.rel.v1.vm.Context;

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
