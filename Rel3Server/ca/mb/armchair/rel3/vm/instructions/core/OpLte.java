package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.values.Value;
import ca.mb.armchair.rel3.values.ValueBoolean;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpLte extends Instruction {
	public final void execute(Context context) {
		Value v2 = context.pop();
		context.push(ValueBoolean.select(context.getGenerator(), context.pop().compareTo(v2)<=0));
	}
}
