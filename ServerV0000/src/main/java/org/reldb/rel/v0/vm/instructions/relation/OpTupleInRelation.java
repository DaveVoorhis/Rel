/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTupleInRelation extends Instruction {
	public final void execute(Context context) {
	    // IN
	    // POP - ValueTuple
	    // POP - ValueRelation
	    // PUSH - ValueBoolean
    	ValueTuple tuple = (ValueTuple)context.pop();
    	ValueRelation relation = (ValueRelation)context.pop();
    	context.push(ValueBoolean.select(context.getGenerator(), relation.contains(tuple)));
	}
}