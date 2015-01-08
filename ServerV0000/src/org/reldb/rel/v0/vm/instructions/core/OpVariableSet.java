package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpVariableSet extends Instruction {

	private int depth;
	private int offset;
	
	public OpVariableSet(int depth, int offset) {
		this.depth = depth;
		this.offset = offset;
	}	
	
	public final void execute(Context context) {
		context.varSet(depth, offset);
	}
		
	public String toString() {
		return getName() + " " + depth + " " + offset;
	}
}
