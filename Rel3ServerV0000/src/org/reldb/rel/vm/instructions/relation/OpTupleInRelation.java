/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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