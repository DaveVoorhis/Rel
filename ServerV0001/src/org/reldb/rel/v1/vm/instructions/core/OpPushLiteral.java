package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.values.Value;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpPushLiteral extends Instruction {

	private Value v;
	
	public OpPushLiteral(Value v) {
		this.v = v;
	}
	
	public final void execute(Context context) {
		context.pushLiteral(v);
	}
	
	public String toString() {
		return getName() + " " + v;
	}
}
