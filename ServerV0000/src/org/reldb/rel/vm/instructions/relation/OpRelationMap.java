/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;

public final class OpRelationMap extends Instruction {
	private Operator tupleOperator;
	
	// ValueOperator of the form OPERATOR(TUPLE x) RETURNS TUPLE
	public OpRelationMap(Operator tupleOperator) {
		this.tupleOperator = tupleOperator;
	}
	
	public final void execute(Context context) {
		// Relation MAP.
		//
		// Applies an operator to each tuple in a relation to produce a new relation.
		// POP - ValueRelation
		// PUSH - ValueRelation
		//
		context.push(((ValueRelation)context.pop()).map(new RelTupleMap(context, tupleOperator)));
	}
}