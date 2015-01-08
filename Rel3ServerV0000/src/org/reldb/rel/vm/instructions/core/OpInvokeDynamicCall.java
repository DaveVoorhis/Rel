package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.generator.OperatorDefinition;
import org.reldb.rel.generator.OperatorSignature;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
