/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.types.AttributeMap;
import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpRelationProject extends Instruction {

	private AttributeMap map;
	
	public OpRelationProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Project the ValueRelation on the stack using the provided AttributeMap.
	    // POP - Value (ValueRelation)
	    // PUSH - Value (ValueRelation)
		context.push(((ValueRelation)context.pop()).project(map));
	}
}