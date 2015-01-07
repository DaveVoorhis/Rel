/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.storage.relvars.Relvar;

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
