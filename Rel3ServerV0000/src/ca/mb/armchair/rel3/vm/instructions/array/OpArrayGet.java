/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpArrayGet extends Instruction {
	
	public final void execute(Context context) {
	    // Obtain value at given subscript in an array
	    //
	    // POP - ValueInteger - subscript
	    // POP - ValueArray - array
	    // PUSH - Value
    	int subscript = (int)((ValueInteger)context.pop()).longValue();
    	ValueArray array = (ValueArray)context.pop();
    	context.push(array.get(subscript));
	}
}