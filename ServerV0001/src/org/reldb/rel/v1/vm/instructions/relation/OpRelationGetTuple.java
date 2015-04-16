/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpRelationGetTuple extends Instruction {

	public final void execute(Context context) {
		// Get tuple from relation of cardinality 1
		//
		// POP - ValueRelation
		// PUSH - ValueTuple
		context.push(((ValueRelation)context.pop()).getTuple());
	}
}