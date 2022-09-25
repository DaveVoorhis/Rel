package org.reldb.rel.v0.vm.instructions.core;

import java.io.PrintStream;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpWriteln extends Instruction {
	
	private Type type;
	
	public OpWriteln(Type type) {
		this.type = type;
	}
	
	public final void execute(Context context) {
		PrintStream output = context.getVirtualMachine().getPrintStream();
		context.pop().toStream(context, type, output, 0);
		output.println();
		output.flush();
	}
}
