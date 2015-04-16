/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpRelationOrder extends Instruction {
	
	private OrderMap map;
	
	public OpRelationOrder(OrderMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Order a relation
	    //
	    // POP - ValueRelation
	    // PUSH - ValueRelation
    	context.push(((ValueRelation)context.pop()).sort(map));
	}
}
