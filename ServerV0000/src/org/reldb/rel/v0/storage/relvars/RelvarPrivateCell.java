package org.reldb.rel.v0.storage.relvars;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.ValueRelationRelvar;
import org.reldb.rel.v0.storage.tables.KeyTables;
import org.reldb.rel.v0.storage.tables.TablePrivate;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.*;

// TODO - optimise RelvarPrivateCell to use in-memory storage for low cardinality
public class RelvarPrivateCell implements Cell, Relvar {
	
	private TablePrivate table;
	
	public RelvarPrivateCell(TablePrivate table) {
		this.table = table;
	}
	
	public Value getValue(Generator generator) {
		return new ValueRelationRelvar(generator, this);
	}

	public void setValue(Generator generator, Value v) {
		setValue((ValueRelation)v);
	}
	
	public void setValue(ValueRelation relation) {
		table.getDatabase().setValue(this, relation);
	}

	public void setTable(KeyTables table) {
		this.table.setTable(table);
	}

	public TablePrivate getTable() {
		return table;
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
	
	public long getCardinality(Generator generator) {
		return table.getCardinality();
	}
	
	public boolean contains(Generator generator, ValueTuple tuple) {
		return table.contains(generator, tuple);
	}
	
	// Delete all tuples
	public void purge() {
		table.purge();
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
