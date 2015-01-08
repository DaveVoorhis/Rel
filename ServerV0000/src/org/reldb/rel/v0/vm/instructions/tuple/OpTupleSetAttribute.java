/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tuple;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleSetAttribute extends Instruction {

	private int index;
	
	public OpTupleSetAttribute(int index) {
		this.index = index;
	}
	
	public final void execute(Context context) {
		context.tupleSetAttribute(index);
	}
}