/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpRelationPushLiteral extends Instruction {
	
	public final void execute(Context context) {
	    // Push relation literal.
	    //
		// PUSH - ValueRelationLiteral
    	context.push(new ValueRelationLiteral(context.getGenerator()));
	}
}