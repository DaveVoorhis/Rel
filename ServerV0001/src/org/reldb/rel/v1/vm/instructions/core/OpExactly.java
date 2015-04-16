/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpExactly extends Instruction {

	private int countOfValues;
	
	public OpExactly(int countOfValues) {
		this.countOfValues = countOfValues;
	}
	
	public final void execute(Context context) {
		context.exactly(context.getGenerator(), countOfValues);
	}
}