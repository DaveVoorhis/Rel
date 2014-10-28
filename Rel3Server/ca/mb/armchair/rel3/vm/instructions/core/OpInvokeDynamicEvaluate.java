package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.OperatorDefinition;
import ca.mb.armchair.rel3.generator.OperatorSignature;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpInvokeDynamicEvaluate extends Instruction {

	private DynamicDispatch dispatcher;
	
	public OpInvokeDynamicEvaluate(Generator generator, OperatorSignature invocation) {
		dispatcher = new DynamicDispatch(generator, invocation, generator.getCurrentOperatorDefinition()) {
			void invoke(OperatorDefinition operator, Context context) {
				operator.evaluate(context);
			}
		};
	}
	
	public final void execute(Context context) {
		dispatcher.locateAndInvoke(context);
	}
}
