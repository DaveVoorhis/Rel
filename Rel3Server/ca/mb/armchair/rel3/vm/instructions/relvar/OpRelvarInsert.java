/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.storage.relvars.Relvar;
import ca.mb.armchair.rel3.values.*;

public final class OpRelvarInsert extends Instruction {

	public final void execute(Context context) {
		// Insert into relvar
		//
		// POP - RelvarUpdatable
		// POP - ValueRelation
		//
		// NOTE: When inserting into a nested relation, context.pop() can be a ValueRelationLiteral.
		//
		long insertCount = ((Relvar)context.pop()).insert(context.getGenerator(), (ValueRelation)context.pop());
		context.getVirtualMachine().noticeInsert(insertCount);
	}
}
