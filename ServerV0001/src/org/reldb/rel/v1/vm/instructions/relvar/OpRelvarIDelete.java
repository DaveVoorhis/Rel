package org.reldb.rel.v1.vm.instructions.relvar;

import org.reldb.rel.v1.storage.relvars.Relvar;
import org.reldb.rel.v1.values.ValueRelation;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpRelvarIDelete extends Instruction {

	@Override
	public void execute(Context context) {
		// Delete tuples from relvar matching those in ValueRelation.  Throw exception if they're not included in the relvar.
		//
		// POP - RelvarUpdatable
		// POP - ValueRelation
		Relvar relvar = (Relvar)context.pop();
		ValueRelation tuplesToDelete = (ValueRelation)context.pop();
		long deleteCount = relvar.delete(context, tuplesToDelete, true);
		context.getVirtualMachine().noticeDelete(deleteCount);
	}

}
