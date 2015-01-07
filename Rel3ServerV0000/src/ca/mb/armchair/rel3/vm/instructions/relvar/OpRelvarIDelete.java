package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.storage.relvars.Relvar;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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
