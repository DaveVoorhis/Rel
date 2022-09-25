package org.reldb.rel.v0.generator;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.*;
import org.reldb.rel.v0.vm.instructions.core.OpNativeFunction;

/** This class captures information about the native function currently being defined.
 * 
 * @author dave
 *
 */
public class OperatorDefinitionNativeFunction extends OperatorDefinitionNative {
	
	private NativeFunction operator;
	private String language = "Java";

	/** Ctor for operator definition. */
	public OperatorDefinitionNativeFunction(String name, String docs, Type[] parameters, Type returnType, NativeFunction fn) {
		super(name, parameters);
		setDeclaredReturnType(returnType);
		operator = fn;
		setSourceCode(docs);
	}
	
	/** Ctor for operator definition. */
	public OperatorDefinitionNativeFunction(OperatorSignature signature, NativeFunction fn) {
		this(signature.getName(), "", signature.getParameterTypes(), signature.getReturnType(), fn);
	}
	
	/** Get primary language, which is 'Java' by default. */
	public String getLanguage() {
		return language;
	}
	
	/** Override the primary language. */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void compileCall(Generator generator) {
		generator.compileInstruction(new OpNativeFunction(operator, getParmCount()));
		generator.compilePop();
	}

	public Type compileEvaluate(Generator generator) {
		generator.compileInstruction(new OpNativeFunction(operator, getParmCount()));
		return getDeclaredReturnType();
	}
	
	public void call(Context context) {
		new OpNativeFunction(operator, getParmCount()).execute(context);
		context.pop();
	}

	public void evaluate(Context context) {
		new OpNativeFunction(operator, getParmCount()).execute(context);
	}

}
