/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.Operator;

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