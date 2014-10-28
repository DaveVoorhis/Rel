/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpArraySet extends Instruction {
	
	public final void execute(Context context) {
	    // Set value at given subscript in an array
	    //
	    // POP - ValueTuple - tuple
	    // POP - ValueInteger - subscript
	    // POP - ValueArray - array
    	ValueTuple value = (ValueTuple)context.pop();
    	int subscript = (int)((ValueInteger)context.pop()).longValue();
    	ValueArray array = (ValueArray)context.pop();
    	array.set(subscript, value);
	}
}