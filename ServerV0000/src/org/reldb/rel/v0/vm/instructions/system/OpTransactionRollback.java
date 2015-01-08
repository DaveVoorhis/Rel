/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTransactionRollback extends Instruction {

	public final void execute(Context context) {
	    // ROLLBACK
		context.getVirtualMachine().getRelDatabase().rollbackTransaction();
	}
}
