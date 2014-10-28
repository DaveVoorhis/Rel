package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.values.ValueInteger;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpAdd extends Instruction {
	public final void execute(Context context) {
		context.push(ValueInteger.select(context.getGenerator(), context.pop().longValue() + context.pop().longValue()));
	}
}
