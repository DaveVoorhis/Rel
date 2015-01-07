/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.NativeProcedure;

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