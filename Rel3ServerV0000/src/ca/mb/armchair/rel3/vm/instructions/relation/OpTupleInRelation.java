/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

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