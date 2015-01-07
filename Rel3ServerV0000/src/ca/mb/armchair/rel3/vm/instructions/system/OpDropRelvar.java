/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpDropRelvar extends Instruction {

	private String name;
	
	public OpDropRelvar(String name) {
		this.name = name;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropRelvar(name);
	}
}
