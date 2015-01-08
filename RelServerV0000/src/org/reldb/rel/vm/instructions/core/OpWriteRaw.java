package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpWriteRaw extends Instruction {
	public final void execute(Context context) {
		System.out.println(context.pop());
	}
}
