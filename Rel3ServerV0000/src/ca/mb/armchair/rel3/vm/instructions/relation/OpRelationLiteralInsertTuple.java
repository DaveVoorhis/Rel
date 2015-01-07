/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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