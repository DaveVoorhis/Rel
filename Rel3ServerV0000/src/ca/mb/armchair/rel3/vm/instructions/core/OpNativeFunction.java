/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.NativeFunction;

public final class OpNativeFunction extends Instruction {

	private NativeFunction function;
	private int parmCount;
	
	public OpNativeFunction(NativeFunction function, int parmCount) {
		this.function = function;
		this.parmCount = parmCount;
	}
	
	public final void execute(Context context) {
		context.userFunction(function, parmCount);
	}
}