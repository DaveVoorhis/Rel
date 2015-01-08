package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpWrite extends Instruction {
	
	private Type type;
	
	public OpWrite(Type type) {
		this.type = type;
	}
	
	public final void execute(Context context) {
		context.pop().toStream(context, type, context.getVirtualMachine().getPrintStream(), 0);
	}
}
