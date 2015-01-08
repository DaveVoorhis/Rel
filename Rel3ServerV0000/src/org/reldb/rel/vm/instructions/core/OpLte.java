package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.values.Value;
import org.reldb.rel.values.ValueBoolean;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpLte extends Instruction {
	public final void execute(Context context) {
		Value v2 = context.pop();
		context.push(ValueBoolean.select(context.getGenerator(), context.pop().compareTo(v2)<=0));
	}
}
