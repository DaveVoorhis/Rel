package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.values.ValueOperator;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpPreserveContextInValueOperator extends Instruction {

	@Override
	public void execute(Context context) {
		((ValueOperator)context.peek()).setEnclosingContext(context);
	}

}
