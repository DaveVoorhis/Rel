/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.values.*;

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