/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.array;

import org.reldb.rel.v1.types.AttributeMap;
import org.reldb.rel.v1.values.ValueArray;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpArrayProject extends Instruction {

	private AttributeMap map;
	
	public OpArrayProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Project the ValueArray on the stack using the provided AttributeMap.
	    // POP - Value (ValueArray)
	    // PUSH - Value (ValueArray)
		context.push(((ValueArray)context.pop()).project(map));
	}
}