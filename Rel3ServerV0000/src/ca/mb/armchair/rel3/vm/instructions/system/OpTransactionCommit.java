/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTransactionCommit extends Instruction {

	public final void execute(Context context) {
	    // COMMIT
		context.getVirtualMachine().getRelDatabase().commitTransaction();
	}
}
