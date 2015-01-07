/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpExactly extends Instruction {

	private int countOfValues;
	
	public OpExactly(int countOfValues) {
		this.countOfValues = countOfValues;
	}
	
	public final void execute(Context context) {
		context.exactly(context.getGenerator(), countOfValues);
	}
}