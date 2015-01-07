/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpRelationGetTuple extends Instruction {

	public final void execute(Context context) {
		// Get tuple from relation of cardinality 1
		//
		// POP - ValueRelation
		// PUSH - ValueTuple
		context.push(((ValueRelation)context.pop()).getTuple());
	}
}