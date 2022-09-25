package org.reldb.rel.v0.generator;

import java.lang.reflect.*;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.instructions.system.OpJavaCall;

/** This class captures information about the native function currently being defined.
 * 
 * @author dave
 *
 */
public class OperatorDefinitionNativeFunctionExternal extends OperatorDefinitionNative {
	
	private Method method;
	
	/** Ctor for operator definition, with resolved class. */
	public OperatorDefinitionNativeFunctionExternal(OperatorSignature sig, Method method) {
		super(sig.getName(), sig.getParameterTypes());
		setDeclaredReturnType(sig.getReturnType());
		this.method = method;
	}
	
	/** Get primary language. */
	public String getLanguage() {
		return "JavaF";
	}
	
	public void compileCall(Generator generator) {
		generator.compileInstruction(new OpJavaCall(method));
		generator.compilePop();
	}

	public Type compileEvaluate(Generator generator) {
		generator.compileInstruction(new OpJavaCall(method));
		return getDeclaredReturnType();
	}

	public void call(Context context) {
		(new OpJavaCall(method)).execute(context);
		context.pop();
	}

	public void evaluate(Context context) {
		(new OpJavaCall(method)).execute(context);
	}
	
}
