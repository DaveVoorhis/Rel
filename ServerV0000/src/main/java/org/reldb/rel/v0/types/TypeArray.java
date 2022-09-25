package org.reldb.rel.v0.types;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.*;

public class TypeArray extends TypeHeading {

	public TypeArray(Heading heading) {
		super(heading);
	}

	public Value getDefaultValue(Generator generator) {
		return new ValueArray(generator);
	}
	
	public static TypeArray getEmptyArrayType() {
		return new TypeArray(new Heading());
	}
	
	public boolean canAccept(Type source) {
		if (source.getClass() != getClass())
			return false;
		return getElementType().canAccept(((TypeArray)source).getElementType());
	}

	public boolean requiresReformatOf(Type type) {
		if (!(type instanceof TypeArray))
			throw new ExceptionSemantic("RS0259: Expected an ARRAY but got a " + type + " in an operator invocation.");
		return getElementType().requiresReformatOf(((TypeArray)type).getElementType());		
	}
	
	public TypeTuple getElementType() {
		return new TypeTuple(heading);
	}
	
	public boolean canHoldElement(Type source) {
		return getElementType().canAccept(source);
	}
	
	public String getSignature() {
		return "ARRAY " + getElementType().getSignature();
	}
		
}
