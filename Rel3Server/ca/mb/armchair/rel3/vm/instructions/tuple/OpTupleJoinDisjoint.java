/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTupleJoinDisjoint extends Instruction {

	public final void execute(Context context) {
		context.tupleJoinDisjoint();
	}
}