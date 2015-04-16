package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.generator.OperatorDefinition;
import org.reldb.rel.v1.generator.OperatorSignature;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public class OpInvokeDynamicCall extends Instruction {
	
	private DynamicDispatch dispatcher;
	
	public OpInvokeDynamicCall(Generator generator, OperatorSignature invocation) {
		dispatcher = new DynamicDispatch(generator, invocation, generator.getCurrentOperatorDefinition()) {
			void invoke(OperatorDefinition operator, Context context) {
				operator.call(context);
				if (operator.getDeclaredReturnType() != null)
					context.pop();
			}			
		};
	}
	
	public final void execute(Context context) {
		dispatcher.locateAndInvoke(context);
	}
}
