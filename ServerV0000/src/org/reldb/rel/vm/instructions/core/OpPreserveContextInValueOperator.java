package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.values.ValueOperator;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpPreserveContextInValueOperator extends Instruction {

	@Override
	public void execute(Context context) {
		((ValueOperator)context.peek()).setEnclosingContext(context);
	}

}
