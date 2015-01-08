/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTransactionCommit extends Instruction {

	public final void execute(Context context) {
	    // COMMIT
		context.getVirtualMachine().getRelDatabase().commitTransaction();
	}
}
