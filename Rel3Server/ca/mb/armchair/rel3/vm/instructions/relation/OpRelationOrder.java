/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.types.*;

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
