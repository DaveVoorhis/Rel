package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;

public class OpInvoke extends Instruction {

	private Operator operator;
	
	public OpInvoke(Operator operator) {
		this.operator = operator;
	}
	
	public final void execute(Context context) {
		context.call(operator);
	}	
}
