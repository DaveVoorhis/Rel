package ca.mb.armchair.rel3.storage.tables;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.relvars.RelvarExternal;
import ca.mb.armchair.rel3.values.RelTupleFilter;
import ca.mb.armchair.rel3.values.RelTupleMap;
import ca.mb.armchair.rel3.values.TupleFilter;
import ca.mb.armchair.rel3.values.TupleIterator;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.values.ValueTuple;
import ca.mb.armchair.rel3.vm.Context;

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
