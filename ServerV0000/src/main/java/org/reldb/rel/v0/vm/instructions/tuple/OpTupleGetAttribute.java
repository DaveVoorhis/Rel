/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tuple;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleGetAttribute extends Instruction {

	private int index;
	
	public OpTupleGetAttribute(int index) {
		this.index = index;
	}
	
	public final void execute(Context context) {
		context.tupleGetAttribute(index);
	}
	
	public String toString() {
		return super.toString() + " at offset " + index;
	}
}