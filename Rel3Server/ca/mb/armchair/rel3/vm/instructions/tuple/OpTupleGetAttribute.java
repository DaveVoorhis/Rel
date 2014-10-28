/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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