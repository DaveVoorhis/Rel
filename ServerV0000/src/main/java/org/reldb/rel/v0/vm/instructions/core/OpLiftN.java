package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpLiftN extends Instruction {
	private int n;
	public OpLiftN(int n) {
		this.n = n;
	}
	public final void execute(Context context) {
		context.liftN(n);
	}
}
