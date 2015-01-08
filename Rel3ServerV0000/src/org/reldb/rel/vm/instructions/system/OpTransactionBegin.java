/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTransactionBegin extends Instruction {

	public final void execute(Context context) {
	    // BEGIN TRANSACTION
		context.getVirtualMachine().getRelDatabase().beginTransaction();
	}
}
