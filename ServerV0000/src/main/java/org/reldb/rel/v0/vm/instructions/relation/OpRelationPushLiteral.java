/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpRelationPushLiteral extends Instruction {
	
	public final void execute(Context context) {
	    // Push relation literal.
	    //
		// PUSH - ValueRelationLiteral
    	context.push(new ValueRelationLiteral(context.getGenerator()));
	}
}