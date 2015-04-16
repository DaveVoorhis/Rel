/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpRelationTClose extends Instruction {

	public final void execute(Context context) {
		// TCLOSE
		//
		// POP - ValueRelation
		// PUSH - ValueRelation
		context.push(((ValueRelation)context.pop()).tclose());
	}
}