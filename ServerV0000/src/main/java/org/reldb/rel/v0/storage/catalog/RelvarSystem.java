package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.*;
import org.reldb.rel.v0.storage.relvars.RelvarReal;
import org.reldb.rel.v0.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;

public class RelvarSystem extends RelvarReal {
	
	public RelvarSystem(String name, RelDatabase database, RelvarRealMetadata metadata) {
		super(name, database, metadata);
	}
	
	private void doesNotSupport(String prompt) {
		throw new ExceptionSemantic("RS0204: The " + getName() + " relvar does not support " + prompt + ".");		
	}
	
	public void setValue(ValueRelation relation) {
		doesNotSupport("assignment");
	}
	
	public long insert(final ValueTuple tuple) {
		doesNotSupport("INSERT");
		return 0;
	}
	
	public long insert(Generator generator, final ValueRelation relation) {
		doesNotSupport("INSERT");
		return 0;
	}
	
	// Delete all tuples
	public void purge() {
		doesNotSupport("DELETE");
	}

	// Delete given tuple
	public void delete(ValueTuple tuple) {
		doesNotSupport("DELETE");
	}
	
	// Delete selected tuples
	public long delete(Context context, Operator whereTupleOperator) {
		doesNotSupport("DELETE");
		return 0;
	}
	
	// Update all tuples using a given update operator
	public long update(final Context context, final Operator updateTupleOperator) {
		doesNotSupport("UPDATE");
		return 0;
	}
	
	// Update selected tuples using a given update operator
	public long update(final Context context, final Operator whereTupleOperator, final Operator updateTupleOperator) {
		doesNotSupport("UPDATE");
		return 0;
	}
	
	private ValueTuple getTupleFor(Generator generator, String name, String definition, String owner) {
		Value[] rawTuple = new Value[] {
			ValueCharacter.select(generator, name),
			ValueCharacter.select(generator, definition),
			ValueCharacter.select(generator, owner),
			ValueInteger.select(generator, getDatabase().getUniqueID())
		};
		return new ValueTuple(generator, rawTuple);
	}
	
	protected ValueTuple getKeyTuple(Generator generator, String name) {
		return getTupleFor(generator, name, "", "");
	}
	
	public void insertInternal(Generator generator, ValueTuple tuple) {
		super.insert(generator, tuple);
	}
	
	public void insertInternal(Generator generator, String name, String definition, String owner) {
		insertInternal(generator, getTupleFor(generator, name, definition, owner));
	}

	public void deleteInternal(Generator generator, ValueTuple keyTuple) {
		super.delete(generator, keyTuple);
	}
	
	public void deleteInternal(Generator generator, String name) {
		deleteInternal(generator, getKeyTuple(generator, name));
	}
	
	public ValueTuple getTupleForKey(Generator generator, String name) {
		return getTupleForKey(generator, getKeyTuple(generator, name));
	}
	
}
