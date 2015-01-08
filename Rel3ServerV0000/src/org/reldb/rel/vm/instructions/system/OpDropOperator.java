/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.generator.OperatorSignature;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpDropOperator extends Instruction {

	private OperatorSignature signature;
	
	public OpDropOperator(OperatorSignature signature) {
		this.signature = signature;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropOperator(signature);
	}
}
