package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.values.ValueInteger;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpAdd extends Instruction {
	public final void execute(Context context) {
		context.push(ValueInteger.select(context.getGenerator(), context.pop().longValue() + context.pop().longValue()));
	}
}
