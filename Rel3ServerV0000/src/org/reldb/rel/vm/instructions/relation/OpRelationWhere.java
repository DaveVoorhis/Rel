/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;

public final class OpRelationWhere extends Instruction {
	private Operator tupleOperator;
	
	// ValueOperator of the form OPERATOR(TUPLE x) RETURNS BOOLEAN
	public OpRelationWhere(Operator tupleOperator) {
		this.tupleOperator = tupleOperator;
	}
	
	public final void execute(Context context) {
		// Relation WHERE.
		//
		// Applies an boolean operator to each tuple in a relation.  Only tuples where 
		// the operator returns true will appear in the result relation.
		//
		// POP - ValueRelation
		// PUSH - ValueRelation
		// 
		context.push(((ValueRelation)context.pop()).select(new RelTupleFilter(context, tupleOperator)));
	}
}