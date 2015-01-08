/**
 * 
 */
package org.reldb.rel.vm.instructions.relvar;

import org.reldb.rel.storage.relvars.Relvar;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpRelvarPurge extends Instruction {

	public final void execute(Context context) {
		// Purge relvar contents
		//
		// POP - RelvarUpdatable
		((Relvar)context.pop()).purge();
	}
}
