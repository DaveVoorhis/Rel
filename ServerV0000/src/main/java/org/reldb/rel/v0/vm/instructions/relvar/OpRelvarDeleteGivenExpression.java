package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.storage.relvars.Relvar;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpRelvarDeleteGivenExpression extends Instruction {

	@Override
	public void execute(Context context) {
		// Delete tuples from relvar matching those in ValueRelation
		//
		// POP - RelvarUpdatable
		// POP - ValueRelation
		Relvar relvar = (Relvar)context.pop();
		ValueRelation tuplesToDelete = (ValueRelation)context.pop();
		long deleteCount = relvar.delete(context, tuplesToDelete, false);
		context.getVirtualMachine().noticeDelete(deleteCount);
	}

}
