package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpWritelnNoExpression extends Instruction {
	public final void execute(Context context) {
		context.getVirtualMachine().getPrintStream().println();
	}
}
