/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.types.*;

public final class OpRelationUngroup extends Instruction {

	private AttributeMap sourceMap;
	private AttributeMap rvaMap;
	private int rvaIndex;
	private int resultDegree;
	
	// sourceMap - from source relation to target relation
	// rvaMap - from relation-valued-attribute in source to target relation
	// rvaIndex - index of relation-valued-attribute in source relation
	// resultDegree - result degree
	public OpRelationUngroup(AttributeMap sourceMap, AttributeMap rvaMap, int rvaIndex, int resultDegree) {
		this.sourceMap = sourceMap;
		this.rvaMap = rvaMap;
		this.rvaIndex = rvaIndex;
		this.resultDegree = resultDegree;
	}
	
	public final void execute(Context context) {
		// Relation UNGROUP.
		//
		// Ungroup a relation.
		// POP - ValueRelation
		// PUSH - ValueRelation
		context.push(((ValueRelation)context.pop()).ungroup(resultDegree, sourceMap, rvaMap, rvaIndex));
	}
}