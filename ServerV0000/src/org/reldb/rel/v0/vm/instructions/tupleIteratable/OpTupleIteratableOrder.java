/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tupleIteratable;

import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleIteratableOrder extends Instruction {
	
	private OrderMap map;
	
	public OpTupleIteratableOrder(OrderMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Order a relation or array
	    //
	    // POP - TupleIteratable
	    // PUSH - TupleIteratable
    	context.push(((TupleIteratable)context.pop()).sort(map));
	}
}
