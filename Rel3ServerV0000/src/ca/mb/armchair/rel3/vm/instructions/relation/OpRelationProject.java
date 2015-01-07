/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.types.AttributeMap;

public final class OpRelationProject extends Instruction {

	private AttributeMap map;
	
	public OpRelationProject(AttributeMap map) {
		this.map = map;
	}
	
	public final void execute(Context context) {
	    // Project the ValueRelation on the stack using the provided AttributeMap.
	    // POP - Value (ValueRelation)
	    // PUSH - Value (ValueRelation)
		context.push(((ValueRelation)context.pop()).project(map));
	}
}