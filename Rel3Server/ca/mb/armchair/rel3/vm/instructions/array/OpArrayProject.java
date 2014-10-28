/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.array;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.ValueArray;
import ca.mb.armchair.rel3.types.AttributeMap;

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