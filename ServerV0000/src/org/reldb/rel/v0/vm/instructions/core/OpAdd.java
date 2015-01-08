package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpAdd extends Instruction {
	public final void execute(Context context) {
		context.push(ValueInteger.select(context.getGenerator(), context.pop().longValue() + context.pop().longValue()));
	}
}
