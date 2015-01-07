/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.values.*;

public final class OpArrayFor extends Instruction {

	private Operator tupleOperator;
	
	// Operator of the form OPERATOR(TUPLE x)
	public OpArrayFor(Operator tupleOperator) {
		this.tupleOperator = tupleOperator;
	}
	
	public final void execute(final Context context) {
		// ARRAY tuple iteration.
		//
		// Applies an operator to each tuple in an array.
		//
		// POP - ValueArray
		// 
    	(new TupleIteration(((ValueArray)context.pop()).iterator()) {
    		public void process(ValueTuple tuple) {
    			context.push(tuple);
    			context.call(tupleOperator);
    		}
    	}).run();
	}
}
