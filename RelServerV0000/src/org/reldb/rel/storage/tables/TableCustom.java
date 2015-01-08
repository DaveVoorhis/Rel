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

public abstract class TableCustom implements TableExternal {

	@Override
	public abstract TupleIterator iterator();

	@Override
	public abstract TupleIterator iterator(Generator generator);

	@Override
	public abstract long getCardinality();

	@Override
	public abstract boolean contains(Generator generator, ValueTuple tuple);

	@Override
	public abstract ValueTuple getTupleForKey(Generator generator, ValueTuple tuple);

	@Override
	public abstract void setValue(RelvarExternal relvar, ValueRelation relation);

	@Override
	public abstract long insert(Generator generator, ValueRelation relation);

	@Override
	public abstract long insert(Generator generator, ValueTuple tuple);

	@Override
	public abstract long insertNoDuplicates(Generator generator, ValueRelation relation);

	@Override
	public abstract void purge();

	@Override
	public abstract void delete(Generator generator, ValueTuple tuple);

	@Override
	public abstract long delete(Generator generator, RelTupleFilter relTupleFilter);

	@Override
	public abstract long delete(Generator generator, TupleFilter filter);

	@Override
	public abstract long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded);

	@Override
	public abstract long update(Generator generator, RelTupleMap relTupleMap);

	@Override
	public abstract long update(Generator generator, RelTupleFilter relTupleFilter, RelTupleMap relTupleMap);

}
