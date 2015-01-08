/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.array;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.Operator;

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
