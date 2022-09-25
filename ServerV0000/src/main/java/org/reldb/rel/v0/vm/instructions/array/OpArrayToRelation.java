/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.array;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpArrayToRelation extends Instruction {
	
	public final void execute(Context context) {
	    // Convert an array to a relation
	    // 
	    // POP - ValueArray
	    // PUSH - ValueRelation
		ValueArray array = (ValueArray)context.pop();
		context.push(array.toRelation());
	}
}
