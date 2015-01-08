/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpRelationTClose extends Instruction {

	public final void execute(Context context) {
		// TCLOSE
		//
		// POP - ValueRelation
		// PUSH - ValueRelation
		context.push(((ValueRelation)context.pop()).tclose());
	}
}