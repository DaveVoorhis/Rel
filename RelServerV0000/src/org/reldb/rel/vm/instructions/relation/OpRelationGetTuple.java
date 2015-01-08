/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpRelationGetTuple extends Instruction {

	public final void execute(Context context) {
		// Get tuple from relation of cardinality 1
		//
		// POP - ValueRelation
		// PUSH - ValueTuple
		context.push(((ValueRelation)context.pop()).getTuple());
	}
}