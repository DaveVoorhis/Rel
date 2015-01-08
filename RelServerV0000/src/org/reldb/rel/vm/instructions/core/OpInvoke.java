package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;

public class OpInvoke extends Instruction {

	private Operator operator;
	
	public OpInvoke(Operator operator) {
		this.operator = operator;
	}
	
	public final void execute(Context context) {
		context.call(operator);
	}	
}
