/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.storage.relvars.Relvar;

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
