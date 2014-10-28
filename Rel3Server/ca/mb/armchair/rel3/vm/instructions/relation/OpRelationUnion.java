/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.ValueRelation;

public final class OpRelationUnion extends Instruction {

	public final void execute(Context context) {
	    // Relation UNION.
	    // POP - ValueRelation
	    // POP - ValueRelation
	    // PUSH - ValueRelation
		ValueRelation v2 = (ValueRelation)context.pop();
		context.push(((ValueRelation)context.pop()).union(v2));
	}
}