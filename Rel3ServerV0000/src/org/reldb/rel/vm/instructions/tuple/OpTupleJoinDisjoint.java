/**
 * 
 */
package org.reldb.rel.vm.instructions.tuple;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTupleJoinDisjoint extends Instruction {

	public final void execute(Context context) {
		context.tupleJoinDisjoint();
	}
}