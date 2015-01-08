package org.reldb.rel.vm.instructions.core;

import java.io.PrintStream;

import org.reldb.rel.types.Type;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
