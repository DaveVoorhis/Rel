package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.values.Value;
import org.reldb.rel.v1.values.ValueBoolean;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpLte extends Instruction {
	public final void execute(Context context) {
		Value v2 = context.pop();
		context.push(ValueBoolean.select(context.getGenerator(), context.pop().compareTo(v2)<=0));
	}
}
