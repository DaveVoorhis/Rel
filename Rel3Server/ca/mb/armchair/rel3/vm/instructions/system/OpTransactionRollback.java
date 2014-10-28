/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTransactionRollback extends Instruction {

	public final void execute(Context context) {
	    // ROLLBACK
		context.getVirtualMachine().getRelDatabase().rollbackTransaction();
	}
}
