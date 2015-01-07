package ca.mb.armchair.rel3.vm.instructions.core;

import java.io.PrintStream;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.types.Type;

public class OpOutput extends Instruction {
	
	private Type type;
	
	public OpOutput(Type type) {
		this.type = type;
	}
	
	public final void execute(Context context) {
		PrintStream output = context.getVirtualMachine().getPrintStream();
		context.pop().toStream(context, type, output, 1);
		output.println();
	}
}
