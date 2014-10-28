package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.storage.relvars.Relvar;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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
