package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;

public abstract class RelvarSpecial extends RelvarGlobal {
	
	RelvarSpecial(String name, RelDatabase database) {
		super(name, database);
	}

	public long getCardinality(Generator generator) {
		TupleIterator iterator = iterator(generator);
		try {
			long count = 0;
			while (iterator.hasNext()) {
				iterator.next();
				count++;
			}
			return count;
		} finally {
			iterator.close();
		}
	}
	
	public boolean contains(Generator generator, final ValueTuple tuple) {
		TupleIterator iterator = iterator(generator);
		try {
			while (iterator.hasNext())
				if (tuple.equals(iterator.next()))
					return true;
			return false;
		} finally {
			iterator.close();
		}
	}

	// Get a TupleIterator
	public abstract TupleIterator iterator(Generator generator);
	
	private void noUpdates() {
		throw new ExceptionSemantic("RS0203: The " + getName() + " relvar is not updateable.");		
	}
	
	public void setValue(ValueRelation relation) {
		noUpdates();
	}
	
	public long insert(Generator generator, final ValueTuple tuple) {		
		noUpdates();
		return 0;
	}
	
	public long insert(Generator generator, final ValueRelation relation) {
		noUpdates();
		return 0;
	}

	public long insertNoDuplicates(Generator generator, final ValueRelation relation) {
		noUpdates();
		return 0;
	}
		
	// Delete all tuples
	public void purge() {
		noUpdates();
	}

	// Delete selected tuples
	public long delete(final Context context, final Operator whereTupleOperator) {
		noUpdates();
		return 0;
	}
	
	// Delete selected tuples
	public long delete(Generator generator, TupleFilter filter) {
		noUpdates();
		return 0;
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.	
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		noUpdates();
		return 0;
	}
	
	// Update all tuples using a given update operator
	public long update(final Context context, final Operator updateTupleOperator) {
		noUpdates();
		return 0;
	}
	
	// Update selected tuples using a given update operator
	public long update(final Context context, final Operator whereTupleOperator, final Operator updateTupleOperator) {
		noUpdates();
		return 0;
	}
	
}
