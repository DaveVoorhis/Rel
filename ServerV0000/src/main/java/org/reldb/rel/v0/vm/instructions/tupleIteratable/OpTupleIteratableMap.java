/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tupleIteratable;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.Operator;

public final class OpTupleIteratableMap extends Instruction {
	private Operator tupleOperator;
	
	// ValueOperator of the form OPERATOR(TUPLE x) RETURNS TUPLE
	public OpTupleIteratableMap(Operator tupleOperator) {
		this.tupleOperator = tupleOperator;
	}
	
	public final void execute(Context context) {
		// TupleIteratable MAP.
		//
		// Applies an operator to each tuple in a relation to produce a new relation.
		// POP - ValueRelation
		// PUSH - ValueRelation
		//
		context.push(((TupleIteratable)context.pop()).map(new RelTupleMap(context, tupleOperator)));
	}
}