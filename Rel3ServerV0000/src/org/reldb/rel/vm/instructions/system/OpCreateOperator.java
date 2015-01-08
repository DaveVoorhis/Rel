/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.generator.OperatorDefinition;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpCreateOperator extends Instruction {
	private OperatorDefinition operator;
	
	public OpCreateOperator(OperatorDefinition operator) {
		this.operator = operator;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().createOperator(context.getGenerator(), operator);
	}
}
