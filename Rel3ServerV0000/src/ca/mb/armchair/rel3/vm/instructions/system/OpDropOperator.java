/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.generator.OperatorSignature;

public final class OpDropOperator extends Instruction {

	private OperatorSignature signature;
	
	public OpDropOperator(OperatorSignature signature) {
		this.signature = signature;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropOperator(signature);
	}
}
