/**
 * 
 */
package org.reldb.rel.vm.instructions.tuple;

import org.reldb.rel.types.JoinMap;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTupleJoin extends Instruction {

	private JoinMap map;
	
	public OpTupleJoin(JoinMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleJoin(map);
	}
}