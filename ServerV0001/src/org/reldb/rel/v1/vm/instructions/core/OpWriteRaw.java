package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpWriteRaw extends Instruction {
	public final void execute(Context context) {
		System.out.println(context.pop());
	}
}
