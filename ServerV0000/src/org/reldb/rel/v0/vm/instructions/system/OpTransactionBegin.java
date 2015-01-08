/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTransactionBegin extends Instruction {

	public final void execute(Context context) {
	    // BEGIN TRANSACTION
		context.getVirtualMachine().getRelDatabase().beginTransaction();
	}
}
