/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.types.JoinMap;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpRelationJoin extends Instruction {
	private JoinMap map;
	
	public OpRelationJoin(JoinMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Relation JOIN.
	    //
	    // Assumes tuples with common attributes.
	    //
	    // POP - ValueRelation
	    // POP - ValueRelation
	    // PUSH - ValueRelation
		Value v2 = context.pop();
		Value v1 = context.pop();
		context.push(((ValueRelation)v1).join(context.getVirtualMachine().getRelDatabase(), map, (ValueRelation)v2));
	}
}
