/**
 * 
 */
package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.NativeProcedure;

public final class OpNativeProcedure extends Instruction {

	private NativeProcedure procedure;
	private int parmCount;
	
	public OpNativeProcedure(NativeProcedure procedure, int parmCount) {
		this.procedure = procedure;
		this.parmCount = parmCount;
	}
	
	public final void execute(Context context) {
		context.userProcedure(procedure, parmCount);
	}
}