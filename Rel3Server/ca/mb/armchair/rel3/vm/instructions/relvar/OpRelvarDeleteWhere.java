/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.storage.relvars.Relvar;

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
