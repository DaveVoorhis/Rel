/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpAverage extends Instruction {

	private int countOfValues;
	
	public OpAverage(int countOfValues) {
		this.countOfValues = countOfValues;
	}
	
	public final void execute(Context context) {
		context.average(context.getGenerator(), countOfValues);
	}
}