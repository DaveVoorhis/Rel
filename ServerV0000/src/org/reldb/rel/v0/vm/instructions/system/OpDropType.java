/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpDropType extends Instruction {

	private String typeName;
	
	public OpDropType(String typeName) {
		this.typeName = typeName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropType(context.getGenerator(), typeName);
	}
}
