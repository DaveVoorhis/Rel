package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpParameterGet extends Instruction {

	private int depth;
	private int offset;
	
	public OpParameterGet(int depth, int offset) {
		this.depth = depth;
		this.offset = offset;
	}
	
	public final void execute(Context context) {
		context.parmGet(depth, offset);
	}
	
	public String toString() {
		return getName() + " " + depth + " " + offset;
	}
}
