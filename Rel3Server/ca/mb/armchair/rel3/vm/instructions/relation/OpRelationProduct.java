/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.ValueRelation;

public final class OpRelationProduct extends Instruction {

	public final void execute(Context context) {
	    // Relation PRODUCT.
	    //
	    // Assumes disjoint tuples.
	    //
	    // POP - ValueRelation
	    // POP - ValueRelation
	    // PUSH - ValueRelation
		ValueRelation v2 = (ValueRelation)context.pop();
		context.push(((ValueRelation)context.pop()).product(v2));
	}
}