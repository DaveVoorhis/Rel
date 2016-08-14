package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

/** Each invocation pushes a ValueInteger serial number unique to this instruction. */
public final class OpGetTemporarilyUniqueInteger extends Instruction {
	public long serial = 0;
	public final void execute(Context context) {
		context.push(ValueInteger.select(context.getGenerator(), serial++));
	}
}
