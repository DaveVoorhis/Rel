/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpAverage extends Instruction {

	private int countOfValues;
	
	public OpAverage(int countOfValues) {
		this.countOfValues = countOfValues;
	}
	
	public final void execute(Context context) {
		context.average(context.getGenerator(), countOfValues);
	}
}