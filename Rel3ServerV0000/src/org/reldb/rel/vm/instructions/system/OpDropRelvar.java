/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpDropRelvar extends Instruction {

	private String name;
	
	public OpDropRelvar(String name) {
		this.name = name;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropRelvar(name);
	}
}
