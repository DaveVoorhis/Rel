/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.system;

import org.reldb.rel.v1.generator.OperatorSignature;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpDropOperator extends Instruction {

	private OperatorSignature signature;
	
	public OpDropOperator(OperatorSignature signature) {
		this.signature = signature;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropOperator(signature);
	}
}
