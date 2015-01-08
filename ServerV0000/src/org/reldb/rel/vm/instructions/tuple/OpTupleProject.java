/**
 * 
 */
package org.reldb.rel.vm.instructions.tuple;

import org.reldb.rel.types.AttributeMap;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTupleProject extends Instruction {

	private AttributeMap map;
	
	public OpTupleProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
		context.tupleProject(map);
	}
}