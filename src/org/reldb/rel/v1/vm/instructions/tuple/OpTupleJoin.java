/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.tuple;

import org.reldb.rel.v1.types.JoinMap;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpTupleJoin extends Instruction {

	private JoinMap map;
	
	public OpTupleJoin(JoinMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleJoin(map);
	}
}