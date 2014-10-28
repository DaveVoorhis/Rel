package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpJump extends Instruction {

	private int address;
	
	public OpJump(int address) {
		this.address = address;
	}
	
	public final void execute(Context context) {
		context.jump(address);
	}
		
	public String toString() {
		return getName() + " " + address;
	}

}
