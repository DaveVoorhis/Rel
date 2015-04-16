package org.reldb.rel.v1.types;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.values.*;

public class TypeArray extends TypeAbstract {

	private TypeTuple elementType;

	public TypeArray(TypeTuple type) {
		elementType = type;
	}

	public Value getDefaultValue(Generator generator) {
		return new ValueArray(generator);
	}
	
	public boolean canAccept(Type source) {
		if (source.getClass() != getClass())
			return false;
		return elementType.canAccept(((TypeArray)source).elementType);
	}

	public boolean requiresReformatOf(Type type) {
		if (!(type instanceof TypeArray))
			throw new ExceptionSemantic("RS0259: Expected an ARRAY but got a " + type + " in an operator invocation.");
		return elementType.requiresReformatOf(((TypeArray)type).getElementType());		
	}
	
	public TypeTuple getElementType() {
		return elementType;
	}
	
	public boolean canHoldElement(Type source) {
		return elementType.canAccept(source);
	}
	
	public String getSignature() {
		return "ARRAY " + elementType.getSignature();
	}
		
}
