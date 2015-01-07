package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.Value;

public class OpPushLiteral extends Instruction {

	private Value v;
	
	public OpPushLiteral(Value v) {
		this.v = v;
	}
	
	public final void execute(Context context) {
		context.pushLiteral(v);
	}
	
	public String toString() {
		return getName() + " " + v;
	}
}
