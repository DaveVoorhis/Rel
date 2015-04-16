package org.reldb.rel.v1.storage;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.relvars.Relvar;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Operator;

/** Relation from a relvar.  This ValueRelation carries update semantics (Relvar) which delegates to its 
 * source Relvar.  Whilst this seems incorrect (relations are not mutable), it simplifies the rest of the system as 
 * compileGet() is used to obtain a ValueRelationRelvar, which in turn is used to update the Relvar.  In short,
 * it's not the relation that changes, it's the relvar that changes -- but the relvar is accessed through the
 * relvar's current relation. 
 * 
 * <p>This may imply a path to VIRTUAL relvar updating, as well as eventually permitting relation-valued expressions to be updated. 
 */
public class ValueRelationRelvar extends ValueRelation implements Relvar {

	private static final long serialVersionUID = 0;

	private Relvar relvar;
	
	public ValueRelationRelvar(Generator generator, Relvar relvar) {
		super(generator);
		this.relvar = relvar;
	}

	public int hashCode() {
		return 0;
	}
	
	@Override
	public TupleIterator newIterator() {
		return relvar.iterator(getGenerator());
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return newIterator();
	}

	@Override
	public long getCardinality(Generator generator) {
		return relvar.getCardinality(generator);
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		return relvar.contains(generator, tuple);
	}

	public void setValue(ValueRelation relation) {
		relvar.setValue(relation);
	}
	
	public long insert(Generator generator, ValueTuple tuple) {
		return relvar.insert(generator, tuple);
	}

	public long insert(Generator generator, ValueRelation relation) {
		return relvar.insert(generator, relation);
	}

	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		return relvar.insertNoDuplicates(generator, relation);
	}
	
	public void purge() {
		relvar.purge();
	}

	// Delete selected tuples
	public long delete(Context context, Operator whereTupleOperator) {
		return relvar.delete(context, whereTupleOperator);
	}

	// Delete selected tuples
	public long delete(Generator generator, TupleFilter filter) {
		return relvar.delete(generator, filter);
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		return relvar.delete(context, tuplesToDelete, errorIfNotIncluded);
	}
	
	// Update all tuples using a given update operator
	public long update(Context context, Operator updateTupleOperator) {
		return relvar.update(context, updateTupleOperator);
	}

	// Update selected tuples using a given update operator
	public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator) {
		return relvar.update(context, whereTupleOperator, updateTupleOperator);
	}
}
