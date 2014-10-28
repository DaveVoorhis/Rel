/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.types.JoinMap;

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
