/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpDropConstraint extends Instruction {

	private String constraintName;
	
	public OpDropConstraint(String constraintName) {
		this.constraintName = constraintName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropConstraint(context.getGenerator(), constraintName);
	}
}
