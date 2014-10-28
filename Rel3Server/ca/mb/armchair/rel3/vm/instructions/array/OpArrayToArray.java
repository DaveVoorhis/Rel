/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;

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