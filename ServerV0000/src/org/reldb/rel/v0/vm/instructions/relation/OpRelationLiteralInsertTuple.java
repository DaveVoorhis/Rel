/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpRelationLiteralInsertTuple extends Instruction {
	
	public final void execute(Context context) {
	    // Insert tuple in ValueRelationLiteral.
	    //
		// POP - ValueTuple
		// POP - ValueRelationLiteral
		// PUSH - ValueRelationLiteral
    	context.relationLiteralInsertTuple();
	}
}