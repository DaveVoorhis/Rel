package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.values.ValueBoolean;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpTrue extends Instruction {
	public final void execute(Context context) {
		context.pushLiteral(ValueBoolean.select(context.getGenerator(), true));
	}
}
