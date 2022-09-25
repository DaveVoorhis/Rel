package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpFalse extends Instruction {
	public final void execute(Context context) {
		context.pushLiteral(ValueBoolean.select(context.getGenerator(), false));
	}
}
