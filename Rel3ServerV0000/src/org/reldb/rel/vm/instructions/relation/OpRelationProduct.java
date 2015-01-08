/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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