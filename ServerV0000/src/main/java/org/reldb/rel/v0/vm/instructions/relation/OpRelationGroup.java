package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.types.AttributeMap;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpRelationGroup extends Instruction {
	
	private AttributeMap orderMap;
	private AttributeMap groupAttributes;

	public OpRelationGroup(AttributeMap orderMap, AttributeMap groupAttributes) {
		this.orderMap = orderMap;
		this.groupAttributes = groupAttributes;
	}

	@Override
	public void execute(Context context) {
	    // Order by attributes in orderMap. For each group of equal orderMap attributes, 
		// return one tuple of orderMap attributes with all groupAttributes in a new relation-valued attribute.
	    //
	    // POP - ValueRelation
	    // PUSH - ValueRelation
    	context.push(((ValueRelation)context.pop()).group(orderMap, groupAttributes));
	}

}
