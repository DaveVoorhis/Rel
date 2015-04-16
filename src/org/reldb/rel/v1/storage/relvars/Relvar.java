package org.reldb.rel.v1.storage.relvars;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.values.TupleFilter;
import org.reldb.rel.v1.values.TupleIterator;
import org.reldb.rel.v1.values.ValueRelation;
import org.reldb.rel.v1.values.ValueTuple;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Operator;

public interface Relvar {
	
	/** Obtain cardinality. */
	abstract public long getCardinality(Generator generator);

	/** Return true if tuple is found in this relvar's relation. */
	abstract public boolean contains(Generator generator, ValueTuple tuple);

	/** Assign a new relation to this relvar. */
	abstract public void setValue(ValueRelation relation);

	/** Return quantity of tuples inserted, i.e., 1 if insert succeeded. */
	abstract public long insert(Generator generator, ValueTuple tuple);

	/** Return quantity of tuples inserted.  Silently ignore duplicates. */
	abstract public long insert(Generator generator, ValueRelation relation);

	/** Insert, rejecting duplicates. */
	abstract public long insertNoDuplicates(Generator generator, ValueRelation relation);

	/** Purge all tuples from this relvar. */
	abstract public void purge();

	/** Delete selected tuples. */
	abstract public long delete(Context context, Operator whereTupleOperator);

	/** Delete selected tuples. */
	abstract public long delete(Generator generator, TupleFilter filter);

	/**  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error. */
	abstract public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded);
	
	/** Update all tuples using a given update operator. */
	abstract public long update(Context context, Operator updateTupleOperator);

	/** Update selected tuples using a given update operator. */
	abstract public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator);

	/** Obtain a tuple iterator. */
	abstract public TupleIterator iterator(Generator generator);
}