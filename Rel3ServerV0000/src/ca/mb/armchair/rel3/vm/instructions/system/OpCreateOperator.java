/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.generator.OperatorDefinition;

public final class OpCreateOperator extends Instruction {
	private OperatorDefinition operator;
	
	public OpCreateOperator(OperatorDefinition operator) {
		this.operator = operator;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().createOperator(context.getGenerator(), operator);
	}
}
