/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.tuple;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpTuplePushLiteral extends Instruction {

	private int attributeCount;
	
	public OpTuplePushLiteral(int attributeCount) {
		this.attributeCount = attributeCount;
	}
	
	public final void execute(Context context) {
		context.pushTupleLiteral(attributeCount);
	}
}