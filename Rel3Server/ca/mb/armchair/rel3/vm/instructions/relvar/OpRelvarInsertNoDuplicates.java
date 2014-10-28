package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.storage.relvars.Relvar;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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
