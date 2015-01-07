package ca.mb.armchair.rel3.generator;

import ca.mb.armchair.rel3.vm.*;
import ca.mb.armchair.rel3.vm.instructions.core.OpNativeFunction;
import ca.mb.armchair.rel3.types.Type;

/** This class captures information about the native function currently being defined.
 * 
 * @author dave
 *
 */
public class OperatorDefinitionNativeFunction extends OperatorDefinitionNative {
	
	private NativeFunction operator;
	private String language = "Java";

	/** Ctor for operator definition. */
	public OperatorDefinitionNativeFunction(String name, Type[] parameters, Type returnType, NativeFunction fn) {
		super(name, parameters);
		setDeclaredReturnType(returnType);
		operator = fn;
	}
	
	/** Ctor for operator definition. */
	public OperatorDefinitionNativeFunction(OperatorSignature signature, NativeFunction fn) {
		this(signature.getName(), signature.getParameterTypes(), signature.getReturnType(), fn);
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
