/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

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