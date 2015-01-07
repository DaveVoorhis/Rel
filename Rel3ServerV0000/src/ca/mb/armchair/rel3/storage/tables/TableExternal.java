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
