package ca.mb.armchair.rel3.types;

import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.OperatorSignature;

public class TypeOperator extends TypeAbstract {

	private static TypeOperator instance = new TypeOperator();
	
	private OperatorSignature signature; 
	
	public TypeOperator() {
		signature = null;
	}

	public TypeOperator(OperatorSignature signature) {
		this.signature = signature;
	}
	
	public static TypeOperator getInstance() {
		return instance;
	}

	public OperatorSignature getOperatorSignature() {
		return signature;
	}
	
	/** Obtain this type's signature. */
	public String getSignature() {
		return "OPERATOR" + ((signature != null) ? (" " + signature) : "");
	}
	
	/** Return true if source can be assigned to variables of this type. */
	public boolean canAccept(Type source) {
		if (source.getClass() != getClass())
			return false;
		if (signature == null)
			return false;
		if (!(source instanceof TypeOperator))
			return false;
		TypeOperator sourceOperatorType = (TypeOperator)source;
		if (sourceOperatorType.signature == null)
			return false;
		return (signature.canBeInvokedBy(sourceOperatorType.signature));
	}
	
	private static ValueOperator NOOP = null;
	
	private static void makeNOPop(Generator generator) {
		if (NOOP == null)
			NOOP = new ValueOperator(generator, new Operator(0), "OPERATOR (); END OPERATOR");
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		makeNOPop(generator);
		return NOOP;
	}

	protected Type canCompare(Type v) {
		throw new ExceptionSemantic("RS0262: Cannot perform comparison between " + this + " and " + v);
	}

}
