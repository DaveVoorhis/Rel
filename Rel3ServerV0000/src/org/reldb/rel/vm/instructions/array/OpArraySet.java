/**
 * 
 */
package org.reldb.rel.vm.instructions.array;

import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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