package org.reldb.rel.v0.vm.instructions.core;

import java.util.HashSet;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.OperatorDefinition;
import org.reldb.rel.v0.generator.OperatorSignature;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueAlpha;
import org.reldb.rel.v0.vm.Context;

abstract class DynamicDispatch {

	private Generator generator;
	private OperatorSignature invocationSignature;
	private OperatorDefinition inOperator;
	
	DynamicDispatch(Generator generator, OperatorSignature invocationSignature, OperatorDefinition inOperator) {
		this.generator = generator;
		this.invocationSignature = invocationSignature;
		this.inOperator = inOperator;
	}
	
	void locateAndInvoke(Context context) {
		// TODO - improve performance by caching operators for given run-time invocationSignatureS.  Cache should be cleared if type hierarchy changes.
		Value[] operands = context.peek(invocationSignature.getParmCount());
		for (int i=0; i<invocationSignature.getParmCount(); i++)
			if (operands[i] instanceof ValueAlpha) {
				Type argumentType = ((ValueAlpha)operands[i]).getType(generator.getDatabase());
				invocationSignature.setParameterType(i, argumentType);
			}
		HashSet<OperatorSignature> possibleTargets = generator.getPossibleTargetSignatures(inOperator, invocationSignature);
		if (possibleTargets.size() == 0)
			throw new ExceptionSemantic("RS0278: No run-time invocation targets found for " + invocationSignature);
		int closestDistance = Integer.MAX_VALUE;
		OperatorSignature closestSignature = null;
		for (OperatorSignature signature: possibleTargets) {
			int distance = signature.getInvocationDistance(invocationSignature);
			if (distance <= closestDistance) {
				closestDistance = distance;
				closestSignature = signature;
			}
		}
		OperatorDefinition operator = generator.locateOperator(inOperator, closestSignature);
		if (operator != null) {
			invoke(operator, context);
			return;
		}			
		throw new ExceptionSemantic("RS0279: Operator '" + closestSignature + "' has not been defined, nor has any compatible operator of the same name.");
	}
	
	abstract void invoke(OperatorDefinition operator, Context context);
}
