/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.array;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpArrayToArray extends Instruction {
	
	public final void execute(final Context context) {
	    // Copy source array to destination array
	    //
	    // POP - ValueArray
	    // PUSH - ValueArray
    	ValueArray source = (ValueArray)context.pop();
    	final ValueArray array = new ValueArray(context.getGenerator());
    	(new TupleIteration(source.iterator()) {
    		public void process(ValueTuple tuple) {
    			array.append(tuple);    			
    		}
    	}).run();
    	context.push(array);
	}
}