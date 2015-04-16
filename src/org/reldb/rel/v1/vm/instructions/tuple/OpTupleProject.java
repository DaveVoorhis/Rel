/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.tuple;

import org.reldb.rel.v1.types.AttributeMap;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpTupleProject extends Instruction {

	private AttributeMap map;
	
	public OpTupleProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleProject(map);
	}
}