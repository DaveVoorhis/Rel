/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.storage.relvars.Relvar;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

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
