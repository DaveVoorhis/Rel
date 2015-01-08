package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpDuplicateUnder extends Instruction {
	public final void execute(Context context) {
		context.duplicateUnder();
	}
}
