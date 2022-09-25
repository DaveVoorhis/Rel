package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.Operator;

public class OpInvoke extends Instruction {

	private Operator operator;
	
	public OpInvoke(Operator operator) {
		this.operator = operator;
	}
	
	public final void execute(Context context) {
		context.call(operator);
	}	
}
