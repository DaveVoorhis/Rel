/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.types.JoinMap;
import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
