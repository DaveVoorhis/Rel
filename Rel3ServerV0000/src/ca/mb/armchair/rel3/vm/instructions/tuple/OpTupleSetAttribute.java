/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTupleSetAttribute extends Instruction {

	private int index;
	
	public OpTupleSetAttribute(int index) {
		this.index = index;
	}
	
	public final void execute(Context context) {
		context.tupleSetAttribute(index);
	}
}