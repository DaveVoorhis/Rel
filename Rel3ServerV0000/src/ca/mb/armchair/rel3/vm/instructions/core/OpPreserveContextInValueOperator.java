package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.values.ValueOperator;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpPreserveContextInValueOperator extends Instruction {

	@Override
	public void execute(Context context) {
		((ValueOperator)context.peek()).setEnclosingContext(context);
	}

}
