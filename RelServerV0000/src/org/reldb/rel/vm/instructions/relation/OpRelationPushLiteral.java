/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpRelationPushLiteral extends Instruction {
	
	public final void execute(Context context) {
	    // Push relation literal.
	    //
		// PUSH - ValueRelationLiteral
    	context.push(new ValueRelationLiteral(context.getGenerator()));
	}
}