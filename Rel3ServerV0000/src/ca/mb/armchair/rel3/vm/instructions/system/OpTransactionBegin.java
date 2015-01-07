/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTransactionBegin extends Instruction {

	public final void execute(Context context) {
	    // BEGIN TRANSACTION
		context.getVirtualMachine().getRelDatabase().beginTransaction();
	}
}
