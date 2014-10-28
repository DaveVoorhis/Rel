/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpRelationTClose extends Instruction {

	public final void execute(Context context) {
		// TCLOSE
		//
		// POP - ValueRelation
		// PUSH - ValueRelation
		context.push(((ValueRelation)context.pop()).tclose());
	}
}