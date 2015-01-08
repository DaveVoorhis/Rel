/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.types.*;
import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
