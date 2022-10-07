package org.reldb.rel.v0.storage.relvars;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.*;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;

public class RelvarVirtual extends RelvarGlobal {
	
	public RelvarVirtual(String name, RelDatabase database) {
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
	public TupleIterator iterator(final Generator generator) {
		// TODO - cache result of virtual relvar evaluation to improve performance
	    return new TupleIterator() {
	    	private ValueRelation getRelationFromVirtualRelvar() {
		    	try {
		    		RelvarVirtualMetadata metadata = (RelvarVirtualMetadata)getRelvarMetadata();
		    		// TODO - using System.out for the output stream should never be a problem, but it's not a good idea, either.
		    		Evaluation eval = Interpreter.evaluateExpression(getDatabase(), metadata.getSourceCode(), System.out);
		    		if (!(eval.getType() instanceof TypeRelation))
		    			throw new ExceptionSemantic("RS0228: VIRTUAL relation-valued variable expected to evaluate to RELATION, but got " + eval.getType());
		    		Heading evalResultHeading = ((TypeRelation)eval.getType()).getHeading();
		    		if (!metadata.getHeadingDefinition(getDatabase()).getHeading().canAccept(evalResultHeading))
		    			throw new ExceptionSemantic("RS0229: VIRTUAL relation-valued variable expected to have heading of " + metadata.getHeadingDefinition(getDatabase()).getHeading() + " but got " + evalResultHeading);
		    		// TODO - Check compatibility of expected vs. actual RelvarHeading here.
		    		return (ValueRelation)eval.getValue();
		    	} catch (ParseException pe) {
		    		throw new ExceptionSemantic("RS0230: Error in VIRTUAL relation-valued variable: " + pe.toString());
		    	}
	    	}
	    	TupleIterator iterator = getRelationFromVirtualRelvar().iterator();
			public boolean hasNext() {
				return iterator.hasNext();
			}
			public ValueTuple next() {
				return iterator.next();
			}
			public void close() {
				iterator.close();
			}
		};
	}
	
	private void noUpdates() {
		throw new ExceptionSemantic("RS0231: VIRTUAL relation-valued variables are not yet updatable.");
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
