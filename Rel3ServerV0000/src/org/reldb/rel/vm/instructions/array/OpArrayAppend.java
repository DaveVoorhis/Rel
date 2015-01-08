/**
 * 
 */
package org.reldb.rel.vm.instructions.array;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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