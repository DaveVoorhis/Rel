/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relvar;

import org.reldb.rel.v1.storage.relvars.Relvar;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.Operator;

public final class OpRelvarUpdateWhere extends Instruction {
	private Operator whereOperator;
	private Operator updateOperator;
	
	public OpRelvarUpdateWhere(Operator whereTupleOperator, Operator updateTupleOperator) {
		this.whereOperator = whereTupleOperator;
		this.updateOperator = updateTupleOperator;
	}
	
	public final void execute(Context context) {
		// Update tuples in relvar where WHERE operator returns true
		//
		// POP - RelvarUpdatable
		long updateCount = ((Relvar)context.pop()).update(context, whereOperator, updateOperator);
		context.getVirtualMachine().noticeUpdate(updateCount);
	}
}
