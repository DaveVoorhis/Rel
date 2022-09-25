/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public final class OpCheckConstraintsAndCommitOrRollback extends Instruction {
	
	private void revert(VirtualMachine vm) {
		vm.getRelDatabase().rollbackTransactionIfThereIsOne();
		vm.clearTupleUpdateNotices();
	}
	
	public final void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		try {
			String failedConstraint = vm.getRelDatabase().checkConstraints(vm.getPrintStream());
			if (failedConstraint != null) {
				revert(vm);
				throw new ExceptionSemantic("RS0285: Update will cause CONSTRAINT " + failedConstraint + " to fail.");
			}
			else
				vm.getRelDatabase().commitTransaction();
		} catch (Throwable t) {
			revert(vm);
			throw t;
		}
	}
}
