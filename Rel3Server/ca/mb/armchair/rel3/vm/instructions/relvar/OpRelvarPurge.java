/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.storage.relvars.Relvar;

public final class OpRelvarPurge extends Instruction {

	public final void execute(Context context) {
		// Purge relvar contents
		//
		// POP - RelvarUpdatable
		((Relvar)context.pop()).purge();
	}
}
