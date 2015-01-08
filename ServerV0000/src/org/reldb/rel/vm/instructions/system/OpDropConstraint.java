/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpDropConstraint extends Instruction {

	private String constraintName;
	
	public OpDropConstraint(String constraintName) {
		this.constraintName = constraintName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropConstraint(context.getGenerator(), constraintName);
	}
}
