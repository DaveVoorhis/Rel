package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.values.ValueBoolean;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpFalse extends Instruction {
	public final void execute(Context context) {
		context.pushLiteral(ValueBoolean.select(context.getGenerator(), false));
	}
}
