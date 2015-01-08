/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.tuple;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpTuplePushLiteral extends Instruction {

	private int attributeCount;
	
	public OpTuplePushLiteral(int attributeCount) {
		this.attributeCount = attributeCount;
	}
	
	public final void execute(Context context) {
		context.pushTupleLiteral(attributeCount);
	}
}