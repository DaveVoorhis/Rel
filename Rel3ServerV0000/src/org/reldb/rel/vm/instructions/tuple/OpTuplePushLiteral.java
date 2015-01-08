/**
 * 
 */
package org.reldb.rel.vm.instructions.tuple;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public final class OpTuplePushLiteral extends Instruction {

	private int attributeCount;
	
	public OpTuplePushLiteral(int attributeCount) {
		this.attributeCount = attributeCount;
	}
	
	public final void execute(Context context) {
		context.pushTupleLiteral(attributeCount);
	}
}