package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
