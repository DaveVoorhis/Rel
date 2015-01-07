package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpBranchIfTrue extends Instruction {

	private int address;
	
	public OpBranchIfTrue(int address) {
		this.address = address;
	}
	
	public final void execute(Context context) {
		context.branchIfTrue(address);
	}
		
	public String toString() {
		return getName() + " " + address;
	}

}
