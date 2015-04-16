/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relvar;

import org.reldb.rel.v1.storage.relvars.Relvar;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.Operator;

public final class OpRelvarUpdate extends Instruction {
	private Operator updateOperator;
	
	public OpRelvarUpdate(Operator updateTupleOperator) {
		this.updateOperator = updateTupleOperator;
	}
	
	public final void execute(Context context) {
		// Update tuples in relvar
		//
		// POP - RelvarUpdatable
		long updateCount = ((Relvar)context.pop()).update(context, updateOperator);
		context.getVirtualMachine().noticeUpdate(updateCount);
	}
}
