package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.OperatorDefinition;
import ca.mb.armchair.rel3.generator.OperatorSignature;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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
