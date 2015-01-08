/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.storage.relvars.Relvar;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.Operator;

public final class OpRelvarDeleteWhere extends Instruction {
	private Operator whereOperator;
	
	public OpRelvarDeleteWhere(Operator whereTupleOperator) {
		this.whereOperator = whereTupleOperator;
	}
	
	public final void execute(Context context) {
		// Delete tuples from relvar where WHERE operator returns true
		//
		// POP - RelvarUpdatable
		long deleteCount = ((Relvar)context.pop()).delete(context, whereOperator);
		context.getVirtualMachine().noticeDelete(deleteCount);
	}
}
