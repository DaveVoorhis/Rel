package org.reldb.rel.generator;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.types.Type;
import org.reldb.rel.vm.*;
import org.reldb.rel.vm.instructions.core.OpNativeProcedure;

/** This class captures information about the native procedure currently being defined.
 * 
 * @author dave
 *
 */
public class OperatorDefinitionNativeProcedure extends OperatorDefinitionNative {
	
	private NativeProcedure operator;

	/** Ctor for operator definition. */
	public OperatorDefinitionNativeProcedure(String name, Type[] parameters, NativeProcedure fn) {
		super(name, parameters);
		operator = fn;
	}
	
	/** Get primary language. */
	public String getLanguage() {
		return "JavaP";
	}

	public void compileCall(Generator generator) {
		generator.compileInstruction(new OpNativeProcedure(operator, getParmCount()));
	}

	private void noEvaluate() {
		throw new ExceptionSemantic("RS0093: Attempt to evaluate " + getSignature() + " which does not have a return value.");		
	}
	
	public Type compileEvaluate(Generator generator) {
		noEvaluate();
		return null;
	}

	public void call(Context context) {
		(new OpNativeProcedure(operator, getParmCount())).execute(context);
	}

	public void evaluate(Context context) {
		noEvaluate();
	}
	
}
