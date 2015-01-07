/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

public final class OpArrayAppend extends Instruction {
	
	public final void execute(Context context) {
	    // Append value to an array
	    //
	    // POP - ValueTuple
	    // POP - ValueArray - array
    	ValueTuple value = (ValueTuple)context.pop();
    	ValueArray array = (ValueArray)context.pop();
    	array.append(value);
	}
}