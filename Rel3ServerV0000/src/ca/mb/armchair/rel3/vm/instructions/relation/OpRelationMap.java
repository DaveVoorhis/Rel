/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.values.*;

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