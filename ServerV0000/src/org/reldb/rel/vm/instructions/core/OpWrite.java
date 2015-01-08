package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.types.Type;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpWrite extends Instruction {
	
	private Type type;
	
	public OpWrite(Type type) {
		this.type = type;
	}
	
	public final void execute(Context context) {
		context.pop().toStream(context, type, context.getVirtualMachine().getPrintStream(), 0);
	}
}
