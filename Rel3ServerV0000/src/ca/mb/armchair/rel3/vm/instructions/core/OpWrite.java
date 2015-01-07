package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.types.Type;

public class OpWrite extends Instruction {
	
	private Type type;
	
	public OpWrite(Type type) {
		this.type = type;
	}
	
	public final void execute(Context context) {
		context.pop().toStream(context, type, context.getVirtualMachine().getPrintStream(), 0);
	}
}
