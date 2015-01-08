/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tuple;

import org.reldb.rel.v0.types.JoinMap;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleJoin extends Instruction {

	private JoinMap map;
	
	public OpTupleJoin(JoinMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleJoin(map);
	}
}