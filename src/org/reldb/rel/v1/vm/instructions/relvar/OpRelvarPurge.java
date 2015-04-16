/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relvar;

import org.reldb.rel.v1.storage.relvars.Relvar;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpRelvarPurge extends Instruction {

	public final void execute(Context context) {
		// Purge relvar contents
		//
		// POP - RelvarUpdatable
		((Relvar)context.pop()).purge();
	}
}
