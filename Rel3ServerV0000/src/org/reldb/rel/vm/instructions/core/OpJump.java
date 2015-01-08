package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
