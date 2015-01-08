package org.reldb.rel.vm.instructions.relvar;

import org.reldb.rel.storage.relvars.Relvar;
import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpRelvarInsertNoDuplicates extends Instruction {

	@Override
	public void execute(Context context) {
		// Insert into relvar
		//
		// POP - RelvarUpdatable
		// POP - ValueRelation
		long insertCount = ((Relvar)context.pop()).insertNoDuplicates(context.getGenerator(), (ValueRelation)context.pop());
		context.getVirtualMachine().noticeInsert(insertCount);
	}

}
