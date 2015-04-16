package org.reldb.rel.v1.storage.relvars;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.tables.TableReal;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Operator;

/** This represents a relvar in the process of being defined.  RelvarInProgress will only exist
 * at compile-time.  By the completion of the run-time execution, none should be left unless
 * a run-time exception has been thrown.
 * 
 * @author scat070
 *
 */
public class RelvarInProgress extends RelvarGlobal {
	
	private RelvarMetadata metadata;
	
	public RelvarInProgress(String name, RelDatabase database, RelvarMetadata metadata) {
		super(name, database);
		this.metadata = metadata;
	}

	RelvarMetadata getRelvarMetadata() {
		return metadata;
	}
	
	private void misuse(String operation) {
		throw new ExceptionFatal("RS0369: Attempt to use " + operation + " in RelvarInProgress.");
	}
	
	TableReal getTable() {
		misuse("getTable");
		return null;
	}
	
	public long getCardinality(Generator generator) {
		misuse("getCardinality");
		return 0;
	}
	
	public boolean contains(Generator generator, ValueTuple tuple) {
		misuse("contains");
		return false;
	}

	public ValueTuple getTupleForKey(ValueTuple tuple) {
		misuse("getTupleForKey");
		return null;
	}
	
	public void setValue(ValueRelation relation) {
		misuse("setValue");
	}
	
	public long insert(Generator generator, ValueTuple tuple) {
		misuse("insert tuple");
		return 0;
	}
	
	public long insert(Generator generator, ValueRelation relation) {
		misuse("insert relation");
		return 0;
	}
	
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		misuse("insert relation");
		return 0;
	}
	
	// Delete all tuples
	public void purge() {
		misuse("purge");
	}

	// Delete given tuple
	public void delete(ValueTuple tuple) {
		misuse("delete tuple");
	}
	
	// Delete selected tuples
	public long delete(Context context, Operator whereTupleOperator) {
		misuse("delete WHERE (operator)");
		return 0;
	}
	
	// Delete selected tuples
	public long delete(Generator generator, TupleFilter filter) {
		misuse("delete WHERE (filter)");
		return 0;
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		misuse("[i_]delete relation");
		return 0;
	}
	
	// Update all tuples using a given update operator
	public long update(Context context, Operator updateTupleOperator) {
		misuse("update");
		return 0;
	}
	
	// Update selected tuples using a given update operator
	public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator) {
		misuse("update WHERE");
		return 0;
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		misuse("iterator");
		return null;
	}
	
}
