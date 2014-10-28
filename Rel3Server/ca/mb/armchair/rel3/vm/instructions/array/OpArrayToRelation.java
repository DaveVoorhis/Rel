/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

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
