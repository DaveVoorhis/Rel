/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.ddl;

import org.reldb.rel.v0.generator.OperatorSignature;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpDropOperator extends Instruction {

	private OperatorSignature signature;
	
	public OpDropOperator(OperatorSignature signature) {
		this.signature = signature;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().dropOperator(context.getGenerator(), signature);
	}
}
