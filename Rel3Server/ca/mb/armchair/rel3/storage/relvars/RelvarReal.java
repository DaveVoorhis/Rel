package ca.mb.armchair.rel3.storage.relvars;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.tables.TableReal;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;

public class RelvarReal extends RelvarGlobal {
	
	private TableReal table;
	
	public RelvarReal(String name, RelDatabase database, RelvarRealMetadata metadata) {
		super(name, database);
		table = new TableReal(database, name, metadata.getHeadingDefinition(database));
	}
	
	public TableReal getTable() {
		return table;
	}
	
	public RelDatabase getDatabase() {
		return table.getDatabase();
	}
	
	public long getCardinality(Generator generator) {
		return table.getCardinality();
	}
	
	public boolean contains(Generator generator, ValueTuple tuple) {
		return table.contains(generator, tuple);
	}

	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple) {
		return table.getTupleForKey(generator, tuple);
	}
	
	public void setValue(ValueRelation relation) {
		getDatabase().setValue(this, relation);
	}
	
	public long insert(Generator generator, ValueTuple tuple) {
		table.insert(generator, tuple);
		return 1;
	}
	
	public long insert(Generator generator, ValueRelation relation) {
		return table.insert(generator, relation);
	}
	
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		return table.insertNoDuplicates(generator, relation);
	}
	
	// Delete all tuples
	public void purge() {
		table.purge();
	}

	// Delete given tuple
	public void delete(Generator generator, ValueTuple tuple) {
		table.delete(generator, tuple);
	}
	
	// Delete selected tuples
	public long delete(Context context, Operator whereTupleOperator) {
		return table.delete(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator));
	}
	
	// Delete selected tuples
	public long delete(Generator generator, TupleFilter filter) {
		return table.delete(generator, filter);
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.	
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		return table.delete(context, tuplesToDelete, errorIfNotIncluded);
	}
	
	// Update all tuples using a given update operator
	public long update(Context context, Operator updateTupleOperator) {
		return table.update(context.getGenerator(), new RelTupleMap(context, updateTupleOperator));
	}
	
	// Update selected tuples using a given update operator
	public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator) {
		return table.update(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator), new RelTupleMap(context, updateTupleOperator));
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return table.iterator(generator);
	}
	
}
