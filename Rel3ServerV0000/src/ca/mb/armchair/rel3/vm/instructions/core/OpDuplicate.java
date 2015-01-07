package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpDuplicate extends Instruction {
	public final void execute(Context context) {
		context.duplicate();
	}
}
