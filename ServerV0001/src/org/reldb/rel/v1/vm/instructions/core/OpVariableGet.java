package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpVariableGet extends Instruction {
	
	private int depth;
	private int offset;
	
	public OpVariableGet(int depth, int offset) {
		this.depth = depth;
		this.offset = offset;
	}	
	
	public final void execute(Context context) {
		context.varGet(depth, offset);
	}
	
	public String toString() {
		return getName() + " " + depth + " " + offset;
	}
}
