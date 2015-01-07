/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.types.AttributeMap;

public final class OpTupleProject extends Instruction {

	private AttributeMap map;
	
	public OpTupleProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleProject(map);
	}
}