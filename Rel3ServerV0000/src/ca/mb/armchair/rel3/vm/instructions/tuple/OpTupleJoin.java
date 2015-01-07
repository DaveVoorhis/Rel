/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.types.JoinMap;

public final class OpTupleJoin extends Instruction {

	private JoinMap map;
	
	public OpTupleJoin(JoinMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleJoin(map);
	}
}