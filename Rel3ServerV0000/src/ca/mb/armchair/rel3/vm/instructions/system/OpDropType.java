/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpDropType extends Instruction {

	private String typeName;
	
	public OpDropType(String typeName) {
		this.typeName = typeName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropType(context.getGenerator(), typeName);
	}
}
