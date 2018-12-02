package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

/** Debugging instruction to dump stack trace. */
public class OpDumpStack extends Instruction {
	public final void execute(Context context) {
		context.dumpstack();
	}
}
