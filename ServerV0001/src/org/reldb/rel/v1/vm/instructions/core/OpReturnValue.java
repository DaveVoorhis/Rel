package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpReturnValue extends Instruction {
	public final void execute(Context context) {
		context.doReturnValue();
	}
}
