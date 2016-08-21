/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tupleIteratable;

import org.reldb.rel.v0.types.AttributeMap;
import org.reldb.rel.v0.values.TupleIteratable;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleIteratableProject extends Instruction {

	private AttributeMap map;
	
	public OpTupleIteratableProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Project the TupleIteratable on the stack using the provided AttributeMap.
	    // POP - Value (ValueRelation)
	    // PUSH - Value (ValueRelation)
		context.push(((TupleIteratable)context.pop()).project(map));
	}
}