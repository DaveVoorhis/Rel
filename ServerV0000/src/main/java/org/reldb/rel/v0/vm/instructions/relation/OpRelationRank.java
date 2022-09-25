/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpRelationRank extends Instruction {
	
	private OrderMap map;
	
	public OpRelationRank(OrderMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Rank a relation
	    //
	    // POP - ValueRelation
	    // PUSH - ValueRelation with each ValueTuple having an appended ValueInteger containing the tuple's rank.
    	context.push(((ValueRelation)context.pop()).rank(map));
	}
}
