/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import org.reldb.rel.v0.generator.OperatorDefinition;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpCreateOperator extends Instruction {
	private OperatorDefinition operator;
	
	public OpCreateOperator(OperatorDefinition operator) {
		this.operator = operator;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().createOperator(context.getGenerator(), operator);
	}
}
