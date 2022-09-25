/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.ddl;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpDropRelvar extends Instruction {

	private String name;
	
	public OpDropRelvar(String name) {
		this.name = name;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropRelvar(name);
	}
}
