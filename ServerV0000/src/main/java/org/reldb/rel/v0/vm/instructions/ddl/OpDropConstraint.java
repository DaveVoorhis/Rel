/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.ddl;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpDropConstraint extends Instruction {

	private String constraintName;
	
	public OpDropConstraint(String constraintName) {
		this.constraintName = constraintName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropConstraint(context.getGenerator(), constraintName);
	}
}
